package com.att.sane.portal.crypto.connector;

import java.io.IOException;
import javax.imageio.IIOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

@Slf4j
@Component
public class BlockchainConnector {

    public Web3j getWeb3j() {
        String infuraUrl = "https://mainnet.infura.io/v3/a3afd9ca6b3946248d5a54fa8fe4a0d1";
        Web3j web3j = Web3j.build(new HttpService(infuraUrl));

        log.info("connected to web3j client");

        return web3j;
    }

    public void checkWeb3jConnectivity() throws IOException {
        log.info("Inside checkWeb3jConnectivity");
        Web3j web3j = getWeb3j();
        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
        log.info("web3j connectivity is working");
        log.info("Outside checkWeb3jConnectivity");
    }
}
