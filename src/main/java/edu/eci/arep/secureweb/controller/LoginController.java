package edu.eci.arep.secureweb.controller;

import edu.eci.arep.secureweb.model.User;
import edu.eci.arep.secureweb.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public RedirectView loginPage() {
        return new RedirectView("/index.html");
    }


    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username    = credentials.get("username");
        String rawPassword = credentials.get("password");

        if (username == null || rawPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "username and password are required"));
        }

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent() && userService.validatePassword(rawPassword, userOpt.get().getHashedPassword())) {
            return ResponseEntity.ok(Map.of("message", "Login successful", "user", username));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
    }
}