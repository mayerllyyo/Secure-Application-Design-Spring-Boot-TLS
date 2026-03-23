package edu.eci.arep.secureweb.model;

/**
 * Represents an application user with a BCrypt-hashed password.
 */
public class User {

    private final String username;
    private final String hashedPassword;

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
