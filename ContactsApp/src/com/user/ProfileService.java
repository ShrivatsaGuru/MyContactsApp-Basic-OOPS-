package com.user;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;

/*
Contacts App : UC-03 User Profile Management
This class updates user profile and password.
It does the following things:
    - Updates full name and email
    - Checks email is not already used by someone else
    - Changes password using old + new password
    - Uses very simple validation helpers
    - Keeps logic very beginner-friendly

@author Developer
@version 3.0
*/

public class ProfileService {

    private UserRepository repo;

    public ProfileService(UserRepository repo) {
        this.repo = repo;
    }

    // Update full name and email (username stays same in UC-03)
    public void updateProfile(User user, String newFullName, String newEmail) {
        // Validate inputs
        ValidationUtils.requireNotEmpty(newFullName, "Full Name");
        ValidationUtils.requireNotEmpty(newEmail, "Email");
        ValidationUtils.validateEmail(newEmail);

        // Check if email is taken by another user
        User existing = repo.findByEmail(newEmail);
        if (existing != null && existing != user) {
            throw new DuplicateUserException("Email already used by another account");
        }

        // Update name
        user.setFullName(newFullName);

        // If email changed, update repo maps
        String oldEmail = user.getEmail();
        if (!oldEmail.equals(newEmail)) {
            user.setEmail(newEmail);
            repo.updateEmail(user, oldEmail, newEmail);
        }
    }

    // Change password: require old password, set new password
    public void changePassword(User user, String oldPassword, String newPassword) {
        ValidationUtils.requireNotEmpty(oldPassword, "Old Password");
        ValidationUtils.requireNotEmpty(newPassword, "New Password");
        ValidationUtils.validateNewPassword(newPassword); // simple rule

        boolean ok = PasswordUtils.verifyPassword(oldPassword, user.getSalt(), user.getPasswordHash());
        if (!ok) {
            throw new ValidationException("Old password is incorrect");
        }

        String newSalt = PasswordUtils.generateSalt();
        String newHash = PasswordUtils.hashPassword(newPassword, newSalt);
        user.setPassword(newHash, newSalt);
    }
}