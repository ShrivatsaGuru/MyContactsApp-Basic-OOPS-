package com.user;

import java.util.HashMap;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;

import java.util.HashMap;

/*
Contacts App : UC-02 User Authentication
This class stores users in memory.
It does the following things:
    - Uses simple HashMaps to store users
    - Checks duplicates by username/email
    - Finds users by username or email for login
    - Keeps storage code very beginner-friendly

@author Developer
@version 2.0

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
}
