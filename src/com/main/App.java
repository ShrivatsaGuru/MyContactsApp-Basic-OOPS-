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
import com.createContact.*;
import java.util.*;



/*
Contacts App : UC-04 Create Contact
This class guides the user after registration.
It does the following things:
    - Registers a new user from console input
    - Asks the user what to do next (menu)
    - Lets the user change their profile details
    - Lets the user add simple contacts
    - Lets the user list all contacts
    - Exits cleanly when the user is done

@author Developer
@version 4.0
*/

public class App {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        UserRepository userRepo = new UserRepository();
        RegistrationService regService = new RegistrationService(userRepo);
        ProfileService profileService = new ProfileService(userRepo);

        ContactRepository contactRepo = new ContactRepository();
        ContactService contactService = new ContactService(contactRepo);

        System.out.println("=== Contacts App (UC-04) ===");
        System.out.println("Step 1: Register a new user");
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
        User currentUser;
        try {
            currentUser = regService.register(req);
            System.out.println("\nRegistration successful!");
            System.out.println("Hello, " + currentUser.getFullName() + " (Type: " + currentUser.getUserType() + ")");
            System.out.println("You can now choose what to do next.\n");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            sc.close();
            return; // stop if registration failed
        }

        // --- Menu loop: let the user choose what to do next ---
        while (true) {
            System.out.println("What would you like to do?");
            System.out.println("1) Change my details (full name / email)");
            System.out.println("2) Add a contact");
            System.out.println("3) List my contacts");
            System.out.println("4) Exit");
            System.out.print("Enter choice (1-4): ");
            String choice = sc.nextLine().trim();

            if ("1".equals(choice)) {
                System.out.println("\n-- Change My Details --");
                System.out.print("New full name: ");
                String newName = sc.nextLine();
                System.out.print("New email: ");
                String newEmail = sc.nextLine();

                try {
                    profileService.updateProfile(currentUser, newName, newEmail);
                    System.out.println("Details updated. New name: " + currentUser.getFullName()
                            + ", New email: " + currentUser.getEmail() + "\n");
                } catch (Exception e) {
                    System.out.println("Update failed: " + e.getMessage() + "\n");
                }

            } else if ("2".equals(choice)) {
                System.out.println("\n-- Add Contact --");
                System.out.print("Contact name: ");
                String contactName = sc.nextLine();

                List<String> phones = new ArrayList<>();
                System.out.print("Phone number: ");
                String p1 = sc.nextLine();
                if (!p1.trim().isEmpty()) phones.add(p1.trim());

                List<String> emails = new ArrayList<>();
                System.out.print("Email address: ");
                String e1 = sc.nextLine();
                if (!e1.trim().isEmpty()) emails.add(e1.trim());

                try {
                    Contact c = contactService.addContact(currentUser, contactName, phones, emails);
                    System.out.println("Contact added! ID: " + c.getId() + "\n");
                } catch (Exception e) {
                    System.out.println("Could not add contact: " + e.getMessage() + "\n");
                }

            } else if ("3".equals(choice)) {
                // List contacts
                System.out.println("\n-- My Contacts --");
                List<Contact> all = contactRepo.getAll(currentUser.getId());
                if (all.isEmpty()) {
                    System.out.println("You have no contacts yet.\n");
                } else {
                    for (Contact c : all) {
                        System.out.println("- " + c.getName());
                        System.out.println("  Phones: " + c.getPhoneNumbers());
                        System.out.println("  Emails: " + c.getEmailAddresses());
                    }
                    System.out.println();
                }

            } else if ("4".equals(choice)) {
                break;

            } else {
                System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.\n");
            }
        }

        sc.close();
    }
}