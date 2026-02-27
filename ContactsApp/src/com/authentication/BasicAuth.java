package com.authentication;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;

/*
Contacts App : UC-02 User Authentication
This class performs basic authentication.
It does the following things:
    - Accepts username and password
    - Finds the user by username
    - Hashes the entered password with stored salt
    - Compares the hash to verify the user
    - Returns the User on success, null on failure

@author Developer
@version 2.0

*/

public class BasicAuth implements Authentication {

    private String username;
    private String password;

    public BasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public User login(UserRepository repo) {
        User user = repo.findByUsername(username);
        if (user == null) return null;

        boolean ok = PasswordUtils.verifyPassword(password, user.getSalt(), user.getPasswordHash());
        return ok ? user : null;
    }
}
