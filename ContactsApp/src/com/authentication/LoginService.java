package com.authentication;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;

/*
Contacts App : UC-02 User Authentication
This class runs the login process.
It does the following things:
    - Uses the Authentication interface
    - Accepts different auth types (basic or oauth)
    - Returns the logged-in User or null
    - Keeps the code very small and easy to read

@author Developer
@version 2.0

*/

public class LoginService {

    private UserRepository repo;

    public LoginService(UserRepository repo) {
        this.repo = repo;
    }

    public User login(Authentication authMethod) {
        return authMethod.login(repo);
    }
}
