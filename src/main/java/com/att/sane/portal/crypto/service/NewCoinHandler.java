package com.att.sane.portal.crypto.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Transaction;

@Component
@Slf4j
public class NewCoinHandler {

    public void handleNewCoin(Transaction transaction) {
        log.info("Inside [handleNewCoin] with transaction hash: {}", transaction.getHash());


        log.info("Outside [handleNewCoin] with transaction hash: {}", transaction.getHash());
    }
}
