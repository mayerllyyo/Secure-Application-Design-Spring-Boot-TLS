package edu.eci.arep.secureweb.util;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;

/**
 * Utility for reading HTTPS URLs using a custom TrustStore.
 *
 */
public class SecureUrlReader {

    /**
     * Reads the content of an HTTPS URL using the default SSLContext
     * (i.e. after calling {@link #initializeTrustStore()}).
     */
    public static String readUrl(String httpsUrl) throws IOException {
        URL url = new URL(httpsUrl);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }

    /**
     * Loads a TrustStore from the path and password configured via environment
     * variables and sets it as the JVM default SSLContext so that all subsequent
     * HTTPS connections use it.
     *
     * @throws Exception if the TrustStore cannot be loaded or the SSLContext cannot be initialised
     */
    public static void initializeTrustStore() throws Exception {
        String trustStorePath = getEnvOrDefault("TRUSTSTORE_PATH", "keystores/myTrustStore");
        String trustStorePassword = getEnvOrDefault("TRUSTSTORE_PASSWORD", "567890");

        File trustStoreFile = new File(trustStorePath);
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(trustStoreFile)) {
            trustStore.load(fis, trustStorePassword.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        SSLContext.setDefault(sslContext);
    }

    private static String getEnvOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }
}
