package com.user;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;
/*
Contacts App : UC-01 User Registration
This class stores basic user information.
It does the following things:
    - Stores user data using private fields
    - Stores hashed password (not plain text)
    - Keeps userType and maxContacts
    - Allows safe reading through getters only

@author Developer
@version 1.0
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

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public UserType getUserType() { return userType; }
}
