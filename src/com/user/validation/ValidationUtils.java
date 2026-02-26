package com.user.validation;

import com.exception.ValidationException;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;


/*
Contacts App : UC-03 User Profile Management
This class provides simple validation.
It does the following things:
    - Checks email contains '@'
    - Checks password length for old/new passwords
    - Ensures values are not empty or null
    - Adds a helper for new password validation in UC-03

@author Developer
@version 3.0
*/

public class ValidationUtils {

    public static void requireNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static void validateEmail(String email) {
        if (!email.contains("@")) {
            throw new ValidationException("Email must contain '@'");
        }
    }

    public static void validatePassword(String password) {
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
    }

    public static void validateNewPassword(String newPassword) {
        // keep same simple rule for beginners
        validatePassword(newPassword);
    }
}
