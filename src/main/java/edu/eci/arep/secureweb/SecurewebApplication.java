package edu.eci.arep.secureweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class SecurewebApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SecurewebApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", getPort()));
        app.run(args);
    }

    /**
     * Reads the server port from the PORT environment variable (12-factor app principle).
     * Falls back to 5000 if the variable is not set.
     */
    static int getPort() {
        String port = System.getenv("PORT");
        if (port != null) {
            return Integer.parseInt(port);
        }
        return 5000;
    }
}
