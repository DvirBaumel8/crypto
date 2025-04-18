package com.att.sane.portal.crypto.service;

import com.att.sane.portal.crypto.listener.LpListener;
import com.att.sane.portal.crypto.model.NewPairEventDto;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoAppEntryService {
    private final LpListener lpListener;

    public void start() throws IOException {
        log.info("Crypto app entry start");
        lpListener.start(this::newPariEventHandler);
        log.info("Crypto app entry end");
    }

    private void newPariEventHandler(NewPairEventDto newPairEventDto) {
        log.info("Inside newPariEventHandler with address: {}", newPairEventDto.getPairAddress());

    }
}
