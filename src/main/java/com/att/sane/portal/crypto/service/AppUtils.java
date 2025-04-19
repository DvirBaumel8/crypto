package com.att.sane.portal.crypto.service;

import java.io.IOException;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthCall;

import static com.att.sane.portal.crypto.model.CommonConstants.FACTORY_ADDRESS_INFURA;
import static com.att.sane.portal.crypto.model.CommonConstants.WETH_ADDRESS;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;
import static org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction;

@UtilityClass
public class AppUtils {
    public static Function createFunction(String functionName, List<Type> addresses, List<TypeReference<?>> typeReferences) {
        return new Function(functionName, addresses, typeReferences);
    }

    public static String encodeFunction(Function function) {
        return FunctionEncoder.encode(function);
    }

    public static EthCall executeEtherCall(Web3j web3j, String encodedFunction, String to) throws IOException {
        return web3j.ethCall(createEthCallTransaction(null, to, encodedFunction), LATEST).send();
    }

    public static List<Type> decodeFunctionResponse(String value, List<TypeReference<Type>> typeReferenceList) {
        return FunctionReturnDecoder.decode(value, typeReferenceList);
    }

}
