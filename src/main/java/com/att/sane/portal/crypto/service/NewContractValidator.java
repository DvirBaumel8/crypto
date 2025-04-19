package com.att.sane.portal.crypto.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint112;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import static com.att.sane.portal.crypto.model.CommonConstants.FACTORY_ADDRESS_INFURA;
import static com.att.sane.portal.crypto.model.CommonConstants.HEXADECIMAL_ZERO;
import static com.att.sane.portal.crypto.model.CommonConstants.WETH_ADDRESS;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;
import static org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction;
import static org.web3j.utils.Convert.Unit.ETHER;

@Component
@Slf4j
public class NewContractValidator {

    public boolean validateNewContract(String transactionHash, Web3j web3j) throws IOException {
        log.info("Inside [validateNewContract] with transaction hash: {}", transactionHash);

        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();

        Optional<TransactionReceipt> transactionReceiptOptional = ethGetTransactionReceipt.getTransactionReceipt();
        if(transactionReceiptOptional.isPresent()) {
            TransactionReceipt transactionReceipt = transactionReceiptOptional.get();
            String contractAddress = transactionReceipt.getContractAddress();
            log.info("Inside [validateNewContract] with transaction hash: {} and contract address: {}", transactionHash, contractAddress);
            boolean isValidContract = isValidContract(contractAddress, web3j);
            log.info("Outside [validateNewContract] with transaction hash: {} and contract address: {} and result: {}", transactionHash, contractAddress, isValidContract);
            return isValidContract;
        }
        log.info("Outside [validateNewContract] with transaction hash: {} and contract address: {} and result: {}", transactionHash, null, false);
        return false;
    }

    private boolean isValidContract(String contractAddress, Web3j web3j) throws IOException {
        log.info("Inside [isValidContract] for contract address: {}", contractAddress);

        boolean functionsExist = isFunctionsExist(contractAddress, web3j);
        if(functionsExist) {
            return isLiquidityExists(web3j, contractAddress);
        }
        else {
            log.info("Outside [isValidContract] for contract address: {} with result: {}", contractAddress, functionsExist);
            return false;
        }
    }

    private boolean isFunctionsExist(String contractAddress, Web3j web3j) throws IOException {
        log.info("Inside [isFunctionsExist] for contract address: {}", contractAddress);

        boolean result = true;
        if(isFunctionMissing("name", contractAddress, web3j)) {
            log.info("new contract is failed due to name function doesn't exist");
            result = false;
        }
        else if(isFunctionMissing("symbol", contractAddress, web3j)) {
            log.info("new contract is failed due to symbol function doesn't exist");
            result = false;
        }
        else if(isFunctionMissing("totalSupply", contractAddress, web3j)) {
            log.info("new contract is failed due to total supply function doesn't exist");
            result = false;
        }

        log.info("Inside [isFunctionsExist] for contract address: {} with result: {}", contractAddress, result);

        return result;
    }

    private boolean isFunctionMissing(String functionName, String contractAddress, Web3j web3j) throws IOException {
        Function nameFunction = new Function(functionName, List.of(), List.of(TypeReference.create(Uint256.class)));
        String encodedName = FunctionEncoder.encode(nameFunction);
        EthCall response = web3j.ethCall(createEthCallTransaction(null, contractAddress, encodedName), LATEST).send();
        List<Type> decode = FunctionReturnDecoder.decode(response.getValue(), nameFunction.getOutputParameters());
        return decode.isEmpty();
    }

    private boolean isLiquidityExists(Web3j web3j, String contractAddress) throws IOException {
        Function getPairFunction = new Function("getPair",
                List.of(new Address(WETH_ADDRESS), new Address(contractAddress)), List.of(TypeReference.create(Address.class)));

        String encodedGetPairFunction = FunctionEncoder.encode(getPairFunction);
        EthCall response = web3j.ethCall(
                        createEthCallTransaction(null, FACTORY_ADDRESS_INFURA, encodedGetPairFunction), LATEST)
                .send();
        String value = response.getValue();
        String pairAddressValue = "0x" + value.substring(26);

        if(!pairAddressValue.equals(HEXADECIMAL_ZERO)) {
            Function getReservesFunction = new Function("getReserves", List.of(),
                    List.of(TypeReference.create(Uint112.class), TypeReference.create(Uint112.class), TypeReference.create(Uint32.class)));
            String encodeGetReservesFunction = FunctionEncoder.encode(getReservesFunction);

            EthCall reservesResponse = web3j.ethCall(
                            Transaction.createEthCallTransaction(null, pairAddressValue, encodeGetReservesFunction), LATEST)
                    .send();

            List<Type> decoded = FunctionReturnDecoder.decode(reservesResponse.getValue(), getReservesFunction.getOutputParameters());
            BigInteger reserve0 = (BigInteger) decoded.getFirst().getValue();
            BigInteger reserve1 = (BigInteger) decoded.get(1).getValue();

            Function token0Function = new Function("token0",
                    List.of(), List.of(TypeReference.create(Address.class)));
            String encodedToken0Function = FunctionEncoder.encode(token0Function);
            EthCall token0Response = web3j.ethCall(
                            Transaction.createEthCallTransaction(null, pairAddressValue, encodedToken0Function), LATEST)
                    .send();

            String token0Value = "0x" + token0Response.getValue().substring(26);
            boolean isWbnbToken0 = token0Value.equalsIgnoreCase(WETH_ADDRESS);
            BigDecimal wbnbReserve = Convert.fromWei(new BigDecimal(isWbnbToken0 ? reserve0 : reserve1), ETHER);
            return wbnbReserve.compareTo(new BigDecimal("1.0")) >= 0;
        }

        return false;
    }
}
