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
import com.editContact.*;

import java.util.*;


import java.time.LocalDate;


/*
Contacts App : UC-11 Contact Type Tagging
This class demonstrates the full flow with tagging contact type.
It does the following things:
    - Registers a user
    - Lets the user manage contacts (add/edit/delete)
    - (UC-11) On add, asks if contact is a Person or Organization and tags it
    - (UC-08) Supports bulk delete and bulk tag add/remove
    - (UC-09) Searches by name, phone, email, tag, or anywhere
    - (UC-10) Filters by tag, date range, or both
    - Keeps the menu simple and beginner-friendly

@author Developer
@version 11.0
*/

public class App {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Setup
        UserRepository userRepo = new UserRepository();
        RegistrationService regService = new RegistrationService(userRepo);
        ProfileService profileService = new ProfileService(userRepo); // UC-03
        ContactRepository contactRepo = new ContactRepository();
        ContactService contactService = new ContactService(contactRepo);

        System.out.println("=== UC-11 Demo: Add Contact with Type Tag (Person/Organization) ===");
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

        // Menu loop (UC-09 search preserved; UC-10 filters preserved; UC-11 add type tag)
        while (true) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1) Add a contact");
            System.out.println("2) Edit a contact");
            System.out.println("3) List my contacts");
            System.out.println("4) Delete a contact");
            System.out.println("5) Bulk delete contacts");
            System.out.println("6) Bulk add a tag");
            System.out.println("7) Bulk remove a tag");
            System.out.println("8) Change my details (name/email)");
            System.out.println("9) Search contacts"); // UC-09
            System.out.println("10) Filter by tag");  // UC-10
            System.out.println("11) Filter by date range (yyyy-MM-dd)"); // UC-10
            System.out.println("12) Filter by tag AND date range");      // UC-10
            System.out.println("13) Exit");
            System.out.print("Enter choice (1-13): ");
            String choice = sc.nextLine().trim();

            if ("1".equals(choice)) {
                // === UC-11 Add Contact with Type Tag ===
                System.out.println("\n-- Add Contact --");

                // Ask for contact type
                System.out.println("Choose contact type:");
                System.out.println("1) Person");
                System.out.println("2) Organization");
                System.out.print("Enter 1 or 2: ");
                String typeChoice = sc.nextLine().trim();

                String typeTag = "person";
                if ("2".equals(typeChoice)) {
                    typeTag = "organization";
                }

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
                    // NOTE: uses the UC-11 overload: addContact(..., contactTypeTag)
                    Contact c = contactService.addContact(user, name, phones, emails, typeTag);
                    System.out.println("Contact added! ID: " + c.getId() + " | Type Tag: " + typeTag);
                } catch (Exception ex) {
                    System.out.println("Could not add contact: " + ex.getMessage());
                }

            } else if ("2".equals(choice)) {
                // Edit contact (UC-05)
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
                printContacts(contactRepo.getAll(user.getId()));

            } else if ("4".equals(choice)) {
                // Delete single (UC-07)
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
                // Bulk delete (UC-08)
                System.out.println("\n-- Bulk Delete Contacts --");
                System.out.println("Enter contact IDs separated by commas:");
                String line = sc.nextLine();
                List<String> ids = parseIds(line);
                int deleted = contactService.bulkDelete(user, ids);
                System.out.println("Deleted " + deleted + " contacts.");

            } else if ("6".equals(choice)) {
                // Bulk add tag (UC-08)
                System.out.println("\n-- Bulk Add Tag --");
                System.out.println("Enter contact IDs separated by commas:");
                String line = sc.nextLine();
                List<String> ids = parseIds(line);
                System.out.print("Enter tag to ADD: ");
                String tag = sc.nextLine().trim();
                try {
                    int updated = contactService.bulkAddTag(user, ids, tag);
                    System.out.println("Tag added to " + updated + " contacts.");
                } catch (Exception ex) {
                    System.out.println("Bulk tag add failed: " + ex.getMessage());
                }

            } else if ("7".equals(choice)) {
                // Bulk remove tag (UC-08)
                System.out.println("\n-- Bulk Remove Tag --");
                System.out.println("Enter contact IDs separated by commas:");
                String line = sc.nextLine();
                List<String> ids = parseIds(line);
                System.out.print("Enter tag to REMOVE: ");
                String tag = sc.nextLine().trim();
                try {
                    int updated = contactService.bulkRemoveTag(user, ids, tag);
                    System.out.println("Tag removed from " + updated + " contacts.");
                } catch (Exception ex) {
                    System.out.println("Bulk tag remove failed: " + ex.getMessage());
                }

            } else if ("8".equals(choice)) {
                // Change user details (UC-03)
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

            } else if ("9".equals(choice)) {
                // Search (UC-09)
                System.out.println("\n-- Search Contacts --");
                System.out.println("Choose field:");
                System.out.println("1) Name  2) Phone  3) Email  4) Tag  5) Anywhere");
                String f = sc.nextLine().trim();
                System.out.print("Enter search text: ");
                String q = sc.nextLine().trim();

                List<Contact> results = new ArrayList<>();
                if ("1".equals(f))       results = contactService.searchByName(user, q);
                else if ("2".equals(f))  results = contactService.searchByPhone(user, q);
                else if ("3".equals(f))  results = contactService.searchByEmail(user, q);
                else if ("4".equals(f))  results = contactService.searchByTag(user, q);
                else if ("5".equals(f))  results = contactService.searchAll(user, q);
                else System.out.println("Invalid field choice.");

                if (!results.isEmpty()) {
                    System.out.println("\nMatches (" + results.size() + "):");
                    printContacts(results);
                } else {
                    System.out.println("No contacts matched your search.");
                }

            } else if ("10".equals(choice)) {
                // Filter by tag (UC-10)
                System.out.println("\n-- Filter: By Tag --");
                System.out.print("Enter tag text (contains): ");
                String tagPart = sc.nextLine().trim();
                List<Contact> results = contactService.filterByTag(user, tagPart);
                printFilterResults(results);

            } else if ("11".equals(choice)) {
                // Filter by date range (UC-10)
                System.out.println("\n-- Filter: By Date Range --");
                System.out.println("Enter dates as yyyy-MM-dd. Leave empty to skip a bound.");
                System.out.print("Start date: ");
                LocalDate start = DateUtils.parseDateOrNull(sc.nextLine());
                System.out.print("End date: ");
                LocalDate end = DateUtils.parseDateOrNull(sc.nextLine());
                List<Contact> results = contactService.filterByDate(user, start, end);
                printFilterResults(results);

            } else if ("12".equals(choice)) {
                // Filter by tag AND date range (UC-10)
                System.out.println("\n-- Filter: By Tag AND Date Range --");
                System.out.print("Enter tag text (contains): ");
                String tagPart = sc.nextLine().trim();
                System.out.println("Enter dates as yyyy-MM-dd. Leave empty to skip a bound.");
                System.out.print("Start date: ");
                LocalDate start = DateUtils.parseDateOrNull(sc.nextLine());
                System.out.print("End date: ");
                LocalDate end = DateUtils.parseDateOrNull(sc.nextLine());
                List<Contact> results = contactService.filterByTagAndDate(user, tagPart, start, end);
                printFilterResults(results);

            } else if ("13".equals(choice)) {
                break;

            } else {
                System.out.println("Invalid choice. Please enter 1-13.");
            }
        }

        sc.close();
    }

    private static List<String> parseIds(String csv) {
        List<String> ids = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return ids;
        for (String p : csv.split(",")) {
            String id = p.trim();
            if (!id.isEmpty()) ids.add(id);
        }
        return ids;
    }

    private static void printContacts(List<Contact> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No contacts.");
            return;
        }
        for (Contact c : list) {
            System.out.println("- ID: " + c.getId());
            System.out.println("  Name: " + c.getName());
            System.out.println("  Phones: " + c.getPhoneNumbers());
            System.out.println("  Emails: " + c.getEmailAddresses());
            System.out.println("  Tags: " + c.getTags());
            System.out.println("  CreatedAt: " + c.getCreatedAt());
        }
    }

    private static void printFilterResults(List<Contact> results) {
        if (results.isEmpty()) {
            System.out.println("No contacts matched your filter.");
        } else {
            System.out.println("Matched contacts: " + results.size());
            printContacts(results);
        }
    }
}

