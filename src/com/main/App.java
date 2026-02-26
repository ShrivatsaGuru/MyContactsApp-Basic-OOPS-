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
Contacts App : UC-02 User Authentication
This class demonstrates registration followed by login.
It does the following things:
    - Accepts input to register a new user
    - Stores the user in an in-memory repository
    - Lets the user choose a login method (Basic or OAuth)
    - Performs authentication and shows the result clearly

@author Developer
@version 2.0
*/

public class App {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserRepository repo = new UserRepository();
        RegistrationService regService = new RegistrationService(repo);
        LoginService loginService = new LoginService(repo);

        System.out.println("=== Contacts App (UC-02) ===");
        System.out.println("Step 1: Register a new user");

        // --- Registration input ---
        UserRegistrationRequest req = new UserRegistrationRequest();

        System.out.print("Enter username: ");
        req.username = sc.nextLine();

        System.out.print("Enter full name: ");
        req.fullName = sc.nextLine();

        System.out.print("Enter email: ");
        req.email = sc.nextLine();

        System.out.print("Enter password: ");
        req.password = sc.nextLine();

        System.out.print("Enter user type (FREE/PREMIUM): ");
        String type = sc.nextLine().trim().toUpperCase();
        req.userType = "PREMIUM".equals(type) ? UserType.PREMIUM : UserType.FREE;

        // --- Register the user ---
        User createdUser = null;
        try {
            createdUser = regService.register(req);
            System.out.println("Registration successful! User ID: " + createdUser.getId());
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            sc.close();
            return; // stop if registration failed
        }

        // --- Authentication ---
        System.out.println("\nStep 2: Login");
        System.out.println("Choose login method: 1) Basic (username+password)  2) OAuth (email only)");
        String choice = sc.nextLine();

        User loggedIn = null;

        if ("1".equals(choice)) {
            // BasicAuth
            System.out.print("Username: ");
            String lu = sc.nextLine();
            System.out.print("Password: ");
            String lp = sc.nextLine();

            Authentication auth = new BasicAuth(lu, lp);
            loggedIn = loginService.login(auth);

            if (loggedIn != null) {
                System.out.println("Login SUCCESS (Basic). Welcome, " + loggedIn.getFullName());
            } else {
                System.out.println("Login FAILED (Basic). Check username/password.");
            }

        } else if ("2".equals(choice)) {
            // OAuthAuth (dummy)
            System.out.print("Email: ");
            String em = sc.nextLine();

            Authentication auth = new OAuthAuth(em);
            loggedIn = loginService.login(auth);

            if (loggedIn != null) {
                System.out.println("Login SUCCESS (OAuth dummy). Welcome, " + loggedIn.getFullName());
            } else {
                System.out.println("Login FAILED (OAuth dummy). Email not found.");
            }

        } else {
            System.out.println("Invalid choice. Please restart and select 1 or 2.");
        }

        sc.close();
    }
}
