package com.user.validation;

import java.security.MessageDigest;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;
import java.security.MessageDigest;

/*
Contacts App : UC-02 User Authentication
This class hashes and verifies passwords.
It does the following things:
    - Creates a very simple salt (time in ms)
    - Hashes salt + password using SHA-256
    - Compares stored hash with a new hash to verify
    - Keeps implementation simple for beginners

NOTE: This is for learning. Not for production use.

/author Developer
@version 2.0

*/

public class PasswordUtils {

    public static String generateSalt() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String hashPassword(String password, String salt) {
        try {
            String text = salt + password;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes());
            return toHex(hash);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        String newHash = hashPassword(password, salt);
        if (newHash == null) return false;
        return newHash.equals(expectedHash);
    }

    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
