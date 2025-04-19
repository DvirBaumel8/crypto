package com.att.sane.portal.crypto.service;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewContractHandler {
    private final NewContractValidator newContractValidator;

    public void handleNewContract(Transaction transaction, Web3j web3j) throws IOException {
        String transactionHash = transaction.getHash();
        log.info("Inside [handleNewCoin] with transaction hash: {}", transactionHash);

        boolean isNewCoin = newContractValidator.validateNewContract(transactionHash, web3j);
        if(isNewCoin) {

        }
        log.info("Outside [handleNewCoin] with transaction hash: {} and without new coin", transaction.getHash());
    }
}
