package com.att.sane.portal.crypto.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;
import static org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction;

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
            //todo - continue;
            return true;
        }
        else {
            log.info("Outside [isValidContract] for contract address: {} with result: {}", contractAddress, functionsExist);
            return functionsExist;
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
        else if(isFunctionMissing("transfer", contractAddress, web3j)) {
            log.info("new contract is failed due to transfer function doesn't exist");
            result = false;
        }

        log.info("Inside [isFunctionsExist] for contract address: {} with result: {}", contractAddress, result);

        return result;
    }

    private boolean isFunctionMissing(String functionName, String contractAddress, Web3j web3j) throws IOException {
        Function nameFunction = new Function(functionName, List.of(), List.of(TypeReference.create(Utf8String.class)));
        String encodedName = FunctionEncoder.encode(nameFunction);
        EthCall response = web3j.ethCall(createEthCallTransaction(null, contractAddress, encodedName), LATEST).send();
        List<Type> decode = FunctionReturnDecoder.decode(response.getValue(), nameFunction.getOutputParameters());
        return decode.isEmpty();
    }

}
