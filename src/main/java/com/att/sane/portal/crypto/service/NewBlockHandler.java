package com.att.sane.portal.crypto.service;

import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewBlockHandler {
    private final NewContractHandler newContractHandler;

    public void handleNewBlock(EthBlock block, Web3j web3j) throws IOException {
        log.info("Inside [handleNewBlock], block number: {}", block.getBlock().getNumber());

        for(EthBlock.TransactionResult<?> transactionResult : block.getBlock().getTransactions()) {
            EthTransaction ethTransaction = web3j.ethGetTransactionByHash(transactionResult.get().toString()).send();

            Optional<Transaction> transactionOptional = ethTransaction.getTransaction();
            if(transactionOptional.isPresent()) {
                Transaction transaction = transactionOptional.get();
                if(transaction.getTo() == null) {
                    log.info("New contract creation");
                    newContractHandler.handleNewContract(transaction, web3j);
                }
            }
        }
        log.info("Outside [handleNewBlock], block number: {}", block.getBlock().getNumber());
    }
}
