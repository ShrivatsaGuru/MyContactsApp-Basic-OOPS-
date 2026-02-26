package com.user;

import java.util.HashMap;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;

import java.util.HashMap;


/*
Contacts App : UC-03 User Profile Management
This class stores users in memory.
It does the following things:
    - Uses HashMaps to store users by username and email
    - Finds users by username or email
    - Saves new users
    - Updates email mapping safely when email changes
    - Keeps code very easy to read for beginners

@author Developer
@version 3.0
*/

public class UserRepository {

    private HashMap<String, User> byUsername = new HashMap<>();
    private HashMap<String, User> byEmail = new HashMap<>();

    public boolean existsByUsername(String username) {
        return byUsername.containsKey(username);
    }

    public boolean existsByEmail(String email) {
        return byEmail.containsKey(email);
    }

    public void save(User user) {
        byUsername.put(user.getUsername(), user);
        byEmail.put(user.getEmail(), user);
    }

    public User findByUsername(String username) {
        return byUsername.get(username);
    }

    public User findByEmail(String email) {
        return byEmail.get(email);
    }

    // Update email mapping when a user's email changes
    public void updateEmail(User user, String oldEmail, String newEmail) {
        byEmail.remove(oldEmail);
        byEmail.put(newEmail, user);
    }
}
