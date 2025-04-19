package com.att.sane.portal.crypto.connector;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import static java.net.Proxy.Type.HTTP;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BASIC;

@Slf4j
@Component
public class BlockchainConnector {

    public Web3j connectToWeb3jByUrl(String url) throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager x509TrustManager = new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        TrustManager[] trustManagers = new TrustManager[] {x509TrustManager};

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustManagers, new SecureRandom());

        Proxy proxy = new Proxy(HTTP, new InetSocketAddress("defrpxy00f.pst.cso.att.com", 8080));
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(BASIC);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .sslSocketFactory(sc.getSocketFactory(), x509TrustManager)
                .hostnameVerifier((hostName, session) -> true)
                .proxy(proxy)
                .build();

        return Web3j.build(new HttpService(url, okHttpClient));
    }

}
