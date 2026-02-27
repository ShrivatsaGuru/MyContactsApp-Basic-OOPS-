package com.main;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;
import com.authentication.*;
import com.registration.RegistrationService;
import com.registration.UserRegistrationRequest;
import com.createContact.*;
import com.editContact.EditContactService;

import java.util.*;


/*
Contacts App : UC-07 Delete Contact
This class demonstrates deleting contacts.
It does the following things:
    - Registers a user
    - Lets the user add and list contacts
    - Lets the user delete a contact by ID
    - Keeps interactions very simple and beginner-friendly

@author Developer
@version 7.0
*/

public class App {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Setup
        UserRepository userRepo = new UserRepository();
        RegistrationService regService = new RegistrationService(userRepo);
        ProfileService profileService = new ProfileService(userRepo); // from UC-03
        ContactRepository contactRepo = new ContactRepository();
        ContactService contactService = new ContactService(contactRepo);

        System.out.println("Step 1: Register a new user");

        // Register user
        UserRegistrationRequest req = new UserRegistrationRequest();
        System.out.print("Username: ");
        req.username = sc.nextLine();
        System.out.print("Full name: ");
        req.fullName = sc.nextLine();
        System.out.print("Email: ");
        req.email = sc.nextLine();
        System.out.print("Password: ");
        req.password = sc.nextLine();
        req.userType = UserType.FREE;

        User user;
        try {
            user = regService.register(req);
            System.out.println("Registered! Hello, " + user.getFullName());
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            sc.close();
            return;
        }

        // Menu loop
        while (true) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1) Add a contact");
            System.out.println("2) Edit a contact");
            System.out.println("3) List my contacts");
            System.out.println("4) Delete a contact");
            System.out.println("5) Change my details (name/email)");
            System.out.println("6) Exit");
            System.out.print("Enter choice (1-6): ");
            String choice = sc.nextLine().trim();

            if ("1".equals(choice)) {
                // Add contact
                System.out.println("\n-- Add Contact --");
                System.out.print("Contact name: ");
                String name = sc.nextLine();

                List<String> phones = new ArrayList<>();
                System.out.print("Phone number (or leave empty): ");
                String p = sc.nextLine().trim();
                if (!p.isEmpty()) phones.add(p);

                List<String> emails = new ArrayList<>();
                System.out.print("Email address (or leave empty): ");
                String e = sc.nextLine().trim();
                if (!e.isEmpty()) emails.add(e);

                try {
                    Contact c = contactService.addContact(user, name, phones, emails);
                    System.out.println("Contact added! ID: " + c.getId());
                } catch (Exception ex) {
                    System.out.println("Could not add contact: " + ex.getMessage());
                }

            } else if ("2".equals(choice)) {
                // Edit contact
                System.out.println("\n-- Edit Contact --");
                System.out.print("Enter Contact ID to edit: ");
                String cid = sc.nextLine().trim();

                EditContactService edit = new EditContactService();

                System.out.print("New name (leave empty to keep): ");
                String n = sc.nextLine();
                if (!n.trim().isEmpty()) edit.newName = n.trim();

                System.out.print("Replace phones? (y/n): ");
                if ("y".equalsIgnoreCase(sc.nextLine().trim())) {
                    List<String> newPhones = new ArrayList<>();
                    System.out.print("Enter phone 1 (or empty to skip): ");
                    String p1 = sc.nextLine().trim();
                    if (!p1.isEmpty()) newPhones.add(p1);
                    System.out.print("Enter phone 2 (or empty to skip): ");
                    String p2 = sc.nextLine().trim();
                    if (!p2.isEmpty()) newPhones.add(p2);
                    edit.newPhones = newPhones;
                }

                System.out.print("Replace emails? (y/n): ");
                if ("y".equalsIgnoreCase(sc.nextLine().trim())) {
                    List<String> newEmails = new ArrayList<>();
                    System.out.print("Enter email 1 (or empty to skip): ");
                    String e1 = sc.nextLine().trim();
                    if (!e1.isEmpty()) newEmails.add(e1);
                    System.out.print("Enter email 2 (or empty to skip): ");
                    String e2 = sc.nextLine().trim();
                    if (!e2.isEmpty()) newEmails.add(e2);
                    edit.newEmails = newEmails;
                }

                try {
                    Contact updated = contactService.editContact(user, cid, edit);
                    System.out.println("Contact updated! New name: " + updated.getName());
                } catch (Exception ex) {
                    System.out.println("Edit failed: " + ex.getMessage());
                }

            } else if ("3".equals(choice)) {
                // List contacts
                System.out.println("\n-- My Contacts --");
                List<Contact> all = contactRepo.getAll(user.getId());
                if (all.isEmpty()) {
                    System.out.println("You have no contacts yet.");
                } else {
                    for (Contact c : all) {
                        System.out.println("- ID: " + c.getId());
                        System.out.println("  Name: " + c.getName());
                        System.out.println("  Phones: " + c.getPhoneNumbers());
                        System.out.println("  Emails: " + c.getEmailAddresses());
                    }
                }

            } else if ("4".equals(choice)) {
                // Delete contact
                System.out.println("\n-- Delete Contact --");
                System.out.print("Enter Contact ID to delete: ");
                String cid = sc.nextLine().trim();

                try {
                    contactService.deleteContact(user, cid);
                    System.out.println("Contact deleted successfully.");
                } catch (Exception ex) {
                    System.out.println("Delete failed: " + ex.getMessage());
                }

            } else if ("5".equals(choice)) {
                // Change user details
                System.out.println("\n-- Change My Details --");
                System.out.print("New full name: ");
                String newName = sc.nextLine();
                System.out.print("New email: ");
                String newEmail = sc.nextLine();
                try {
                    profileService.updateProfile(user, newName, newEmail);
                    System.out.println("Updated. Name: " + user.getFullName() + ", Email: " + user.getEmail());
                } catch (Exception ex) {
                    System.out.println("Update failed: " + ex.getMessage());
                }

            } else if ("6".equals(choice)) {
                break;

            } else {
                System.out.println("Invalid choice. Please enter 1-6.");
            }
        }

        sc.close();
    }
}

