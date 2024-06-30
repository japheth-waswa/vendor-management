package com.vendor.management.system.domain.util;

import com.cyberark.conjur.api.Conjur;
import com.cyberark.conjur.api.Credentials;
import com.vendor.management.system.domain.valueobject.ConjurDatabaseConfig;
import com.vendor.management.system.domain.valueobject.ConjurUserService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

@Slf4j
@Configuration
public class GenericBean {
    private final Dotenv dotenv;

    public GenericBean(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    private SSLContext conjurSSLContext() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        //todo consider converting this .pem to .der & storing in keystore
        InputStream is = getClass().getResourceAsStream("/conjur-elijahConjurAcc.pem");

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(is);

        // Create a new keystore and add the certificate
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null, null); // Initialize an empty keystore
        keystore.setCertificateEntry("conjur-default", cert);

        // Create a TrustManager that trusts the Conjur server's certificate
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keystore);

        // Create an SSLContext that uses the TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }

    @Bean
    public ConjurDatabaseConfig conjurDatabaseConfig() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        return new ConjurDatabaseConfig(conjurHostDatabaseConfig());
    }

    private Conjur conjurHostDatabaseConfig() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        Credentials credentials = new Credentials("host/" + System.getProperty("CONJUR_AUTHN_LOGIN_HOST_DATABASE_CONFIG"),
                System.getProperty("CONJUR_AUTHN_API_KEY_HOST_DATABASE_CONFIG"));
        return new Conjur(credentials, conjurSSLContext());
    }

    @Bean
    public ConjurUserService conjurUserService() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        return new ConjurUserService(conjurHostUserService());
    }

    private Conjur conjurHostUserService() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        Credentials credentials = new Credentials("host/" + System.getProperty("CONJUR_AUTHN_LOGIN_HOST_USER_SERVICE"),
                System.getProperty("CONJUR_AUTHN_API_KEY_HOST_USER_SERVICE"));
        return new Conjur(credentials, conjurSSLContext());
    }
}
