package com.authentication;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;
/*
Contacts App : UC-02 User Authentication
This interface defines a simple authentication action.
It does the following things:
    - Declares a method to try logging in
    - Works with different login methods (polymorphism)
    - Returns the User on success or null on failure
    - Keeps design extremely simple for beginners

@author Developer
@version 2.0

*/
public interface Authentication {
    User login(UserRepository repo);
}