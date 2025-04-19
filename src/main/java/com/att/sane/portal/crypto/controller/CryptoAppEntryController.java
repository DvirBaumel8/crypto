package com.att.sane.portal.crypto.controller;

import com.att.sane.portal.crypto.connector.BlockchainConnector;
import com.att.sane.portal.crypto.connector.BnbConnector;
import com.att.sane.portal.crypto.connector.InfuraConnector;
import com.att.sane.portal.crypto.service.CryptoAppEntryService;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CryptoAppEntryController {
    private final CryptoAppEntryService cryptoAppEntryService;
    private final InfuraConnector infuraConnector;
    private final BnbConnector bnbConnector;

    @PostMapping("/entry")
    public void cryptoAppEntry() throws NoSuchAlgorithmException, KeyManagementException {
        cryptoAppEntryService.start();
    }

    @GetMapping("/web3j-infura-connectivity")
    public void checkWeb3jInfuraConnectivity() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        infuraConnector.checkInfuraConnectivity();
    }

    @GetMapping("/web3j-bnb-connectivity")
    public void checkWeb3jBnbConnectivity() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        bnbConnector.checkBnbConnectivity();
    }
}
