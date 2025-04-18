package com.att.sane.portal.crypto.controller;

import com.att.sane.portal.crypto.connector.BlockchainConnector;
import com.att.sane.portal.crypto.service.CryptoAppEntryService;
import java.io.IOException;
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
    private final BlockchainConnector blockchainConnector;

    @PostMapping("/entry")
    public void cryptoAppEntry() throws IOException {
        cryptoAppEntryService.start();
    }

    @GetMapping("/web3j-connectivity")
    public void checkWeb3jConnectivity() throws IOException {
        blockchainConnector.checkWeb3jConnectivity();
    }
}
