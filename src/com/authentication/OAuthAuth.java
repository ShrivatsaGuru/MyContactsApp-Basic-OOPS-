package com.authentication;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;

/*
Contacts App : UC-02 User Authentication
This class simulates a simple OAuth login.
It does the following things:
    - Accepts an email address
    - Finds the user by email
    - Returns the User if found (no password needed)
    - Acts as a dummy OAuth for learning purposes

@author Developer
@version 2.0

*/

public class OAuthAuth implements Authentication {

    private String email;

    public OAuthAuth(String email) {
        this.email = email;
    }

    @Override
    public User login(UserRepository repo) {
        // Dummy: if email exists, consider it authenticated
        return repo.findByEmail(email);
    }
}
