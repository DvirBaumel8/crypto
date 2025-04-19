package com.att.sane.portal.crypto.service;

import com.att.sane.portal.crypto.listener.LpListener;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoAppEntryService {
    private final LpListener lpListener;

    public void start() throws NoSuchAlgorithmException, KeyManagementException {
        log.info("Crypto app entry start");
        lpListener.start();
        log.info("Crypto app entry end");
    }

}
