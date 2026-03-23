package edu.eci.arep.secureweb.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple REST endpoint that requires an authenticated user.
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    @PreAuthorize("isAuthenticated()")
    public String hello() {
        return "Greetings from Spring Boot over HTTPS!";
    }

    @GetMapping("/")
    public String index() {
        return "Secure Application Design – use /hello or the /login endpoint.";
    }
}
