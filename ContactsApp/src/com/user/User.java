package com.user;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;


/*
Contacts App : UC-03 User Profile Management
This class stores basic user information.
It does the following things:
    - Stores user data using private fields
    - Stores hashed password (not plain text)
    - Allows safe reading through getters
    - Allows simple updates for full name, email, and password
    - Keeps code very small and easy to understand

@author Developer
@version 3.0
*/

public class User {

    private String id;
    private String username;
    private String fullName;
    private String email;
    private String passwordHash;
    private String salt;
    private UserType userType;

    public User(String id, String username, String fullName, String email,
                String passwordHash, String salt, UserType type) {

        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.userType = type;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public UserType getUserType() { return userType; }

    // Simple setters for UC-03
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }

    // For password change (hash and salt set together)
    public void setPassword(String newHash, String newSalt) {
        this.passwordHash = newHash;
        this.salt = newSalt;
    }
}
