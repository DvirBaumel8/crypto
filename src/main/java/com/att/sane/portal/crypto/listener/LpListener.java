package com.att.sane.portal.crypto.listener;

import com.att.sane.portal.crypto.connector.BlockchainConnector;
import com.att.sane.portal.crypto.model.NewPairEventDto;
import com.att.sane.portal.crypto.service.NewBlockHandler;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;

import static com.att.sane.portal.crypto.model.CommonConstants.BNB_URL;
import static com.att.sane.portal.crypto.model.CommonConstants.INFURA_URL;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

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

    public void start() throws NoSuchAlgorithmException, KeyManagementException {
        Web3j web3j = blockchainConnector.connectToWeb3jByUrl(INFURA_URL);

        disposable = web3j.blockFlowable(false).subscribe(block -> {
            log.info("new block found, block number: {}", block.getBlock().getNumber());
            newBlockHandler.handleNewBlock(block, web3j);
        });
    }

    public void stop() {
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

}