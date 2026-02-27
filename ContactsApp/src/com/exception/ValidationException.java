package com.exception;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;

/*
Contacts App : UC-01 User Registration
This class represents validation errors.
It does the following things:
    - Shows what went wrong when input is invalid
    - Helps keep code clean and readable for beginners

@author Developer
@version 1.0
*/

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
