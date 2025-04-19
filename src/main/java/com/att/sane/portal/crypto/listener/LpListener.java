package com.att.sane.portal.crypto.listener;

import com.att.sane.portal.crypto.connector.BlockchainConnector;
import com.att.sane.portal.crypto.service.NewBlockHandler;
import io.reactivex.disposables.Disposable;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import static com.att.sane.portal.crypto.model.CommonConstants.INFURA_URL;

@Slf4j
@Component
public class LpListener {
    private final BlockchainConnector blockchainConnector;
    private final NewBlockHandler newBlockHandler;
    private Disposable disposable;

    public LpListener(BlockchainConnector blockchainConnector, NewBlockHandler newBlockHandler) {
        this.blockchainConnector = blockchainConnector;
        this.newBlockHandler = newBlockHandler;
    }

    public void scan() throws NoSuchAlgorithmException, KeyManagementException {
        log.info("Inside LpListener.scan");
        Web3j web3j = blockchainConnector.connectToWeb3jByUrl(INFURA_URL);

        disposable = web3j.blockFlowable(false).subscribe(block -> {
            log.info("new block found, block number: {}", block.getBlock().getNumber());
            newBlockHandler.handleNewBlock(block, web3j);
        });

        log.info("Outside LpListener.scan");
    }

    public void stop() {
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

}