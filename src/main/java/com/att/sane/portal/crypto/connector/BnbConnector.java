package com.att.sane.portal.crypto.connector;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import static com.att.sane.portal.crypto.model.CommonConstants.BNB_URL;

@Component
@RequiredArgsConstructor
@Slf4j
public class BnbConnector {
    private final BlockchainConnector blockchainConnector;

    public void checkBnbConnectivity() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        log.info("Inside checkBnbConnectivity");
        Web3j web3j = blockchainConnector.connectToWeb3jByUrl(BNB_URL);

        web3j.web3ClientVersion().send();
        log.info("web3j is connected to bnb");
        log.info("Outside checkBnbConnectivity");
    }
}
