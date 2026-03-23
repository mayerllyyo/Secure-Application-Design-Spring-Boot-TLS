package edu.eci.arep.secureweb.service;

import edu.eci.arep.secureweb.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory user service.  Passwords are always stored as BCrypt hashes –
 * the plain-text password never leaves this class.
 */
@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, User> users = new HashMap<>();

    public UserService() {
        registerUser("admin", "admin123");
        registerUser("user", "password");
    }

    /**
     * Registers a new user, storing only the BCrypt hash of the password.
     *
     * @param username plain-text username
     * @param rawPassword plain-text password (never stored)
     */
    public void registerUser(String username, String rawPassword) {
        String hashed = passwordEncoder.encode(rawPassword);
        users.put(username, new User(username, hashed));
    }

    /**
     * Returns the {@link User} with the given username, if present.
     */
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    /**
     * Validates a plain-text password against the stored BCrypt hash.
     *
     * @return {@code true} if the password matches
     */
    public boolean validatePassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
