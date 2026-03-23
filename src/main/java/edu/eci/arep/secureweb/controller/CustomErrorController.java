package edu.eci.arep.secureweb.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global error handler for unhandled exceptions and HTTP errors.
 * Maps the /error endpoint to return JSON error responses instead of the Whitelabel error page.
 */

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String message    = (String)  request.getAttribute("jakarta.servlet.error.message");

        if (statusCode == null) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        String errorMessage = (message != null && !message.isBlank())
                ? message
                : HttpStatus.valueOf(statusCode).getReasonPhrase();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error",   true);
        body.put("status",  statusCode);
        body.put("message", errorMessage);
        body.put("path",    request.getRequestURI());

        return ResponseEntity.status(statusCode).body(body);
    }
}
