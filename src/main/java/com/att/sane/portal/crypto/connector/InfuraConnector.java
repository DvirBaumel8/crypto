package com.att.sane.portal.crypto.connector;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import static com.att.sane.portal.crypto.model.CommonConstants.INFURA_URL;

@Component
@Slf4j
@RequiredArgsConstructor
public class InfuraConnector {
    private final BlockchainConnector blockchainConnector;

    public void checkInfuraConnectivity() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        log.info("Inside checkInfuraConnectivity");

        Web3j web3j = blockchainConnector.connectToWeb3jByUrl(INFURA_URL);

        web3j.web3ClientVersion().send().getWeb3ClientVersion();
        log.info("web3j is connected to infura");

        log.info("Outside checkInfuraConnectivity");
    }
}
