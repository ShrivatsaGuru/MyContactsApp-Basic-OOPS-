package com.main;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;
import java.util.Scanner;
import com.authentication.*;
import com.registration.RegistrationService;
import com.registration.UserRegistrationRequest;


import java.util.Scanner;

/*
Contacts App : UC-03 User Profile Management
This class demonstrates profile updates and password change.
It does the following things:
    - Registers a user from input
    - Logs in using Basic or OAuth (dummy)
    - Updates full name and email
    - Changes password (old -> new)
    - Logs in again to confirm the new password works

@author Developer
@version 3.0
*/

public class App {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserRepository repo = new UserRepository();
        RegistrationService regService = new RegistrationService(repo);
        LoginService loginService = new LoginService(repo);
        ProfileService profileService = new ProfileService(repo);

        System.out.println("=== UC-03 Demo ===");
        System.out.println("Step 1: Register a new user");

        // Register
        UserRegistrationRequest req = new UserRegistrationRequest();
        System.out.print("Enter username: ");
        req.username = sc.nextLine();
        System.out.print("Enter full name: ");
        req.fullName = sc.nextLine();
        System.out.print("Enter email: ");
        req.email = sc.nextLine();
        System.out.print("Enter password: ");
        req.password = sc.nextLine();
        req.userType = UserType.FREE;

        User user;
        try {
            user = regService.register(req);
            System.out.println("Registered! ID: " + user.getId());
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            sc.close();
            return;
        }

        // Login once (Basic)
        System.out.println("\nStep 2: Login (Basic)");
        System.out.print("Username: ");
        String lu = sc.nextLine();
        System.out.print("Password: ");
        String lp = sc.nextLine();

        User logged = loginService.login(new BasicAuth(lu, lp));
        if (logged == null) {
            System.out.println("Login failed. Exiting.");
            sc.close();
            return;
        }
        System.out.println("Login success! Hello, " + logged.getFullName());

        // Update profile
        System.out.println("\nStep 3: Update Profile");
        System.out.print("New full name: ");
        String newName = sc.nextLine();
        System.out.print("New email: ");
        String newEmail = sc.nextLine();

        try {
            profileService.updateProfile(logged, newName, newEmail);
            System.out.println("Profile updated! New name: " + logged.getFullName()
                    + ", New email: " + logged.getEmail());
        } catch (Exception e) {
            System.out.println("Profile update failed: " + e.getMessage());
        }

        // Change password
        System.out.println("\nStep 4: Change Password");
        System.out.print("Old password: ");
        String oldPw = sc.nextLine();
        System.out.print("New password: ");
        String newPw = sc.nextLine();

        try {
            profileService.changePassword(logged, oldPw, newPw);
            System.out.println("Password changed successfully!");
        } catch (Exception e) {
            System.out.println("Password change failed: " + e.getMessage());
        }

        // Login again with new password
        System.out.println("\nStep 5: Login Again (Basic) with new password");
        System.out.print("Username: ");
        String lu2 = sc.nextLine();
        System.out.print("Password: ");
        String lp2 = sc.nextLine();

        User logged2 = loginService.login(new BasicAuth(lu2, lp2));
        if (logged2 != null) {
            System.out.println("Login success (after password change)! Hi, " + logged2.getFullName());
        } else {
            System.out.println("Login failed with new password.");
        }

        sc.close();
    }
}
