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
Contacts App : UC-12 Tag Management
This class demonstrates full contact usage.
It does the following things:
    - Registers a user
    - Lets the user manage contacts (add/edit/delete)
    - (UC-11) On add, asks if contact is Person or Organization
    - (UC-08) Bulk tagging and deleting
    - (UC-09) Searching contacts
    - (UC-10) Filtering contacts by tag/date
    - (UC-12) Add multiple tags to a contact or remove one tag
    - Keeps menu beginner-friendly and complete

@author Developer
@version 12.0
*/

public class App {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Setup
        UserRepository userRepo = new UserRepository();
        RegistrationService regService = new RegistrationService(userRepo);
        ProfileService profileService = new ProfileService(userRepo);
        ContactRepository contactRepo = new ContactRepository();
        ContactService contactService = new ContactService(contactRepo);

        System.out.println("=== UC-12: Tag Management ===");
        System.out.println("Step 1: Register a new user");

        UserRegistrationRequest req = new UserRegistrationRequest();
        System.out.print("Username: "); req.username = sc.nextLine();
        System.out.print("Full name: "); req.fullName = sc.nextLine();
        System.out.print("Email: "); req.email = sc.nextLine();
        System.out.print("Password: "); req.password = sc.nextLine();
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

        // === MENU LOOP ===
        while (true) {

            System.out.println("\nWhat would you like to do?");
            System.out.println("1) Add a contact");
            System.out.println("2) Edit a contact");
            System.out.println("3) List my contacts");
            System.out.println("4) Delete a contact");
            System.out.println("5) Bulk delete contacts");
            System.out.println("6) Bulk add a tag");
            System.out.println("7) Bulk remove a tag");
            System.out.println("8) Change my details");
            System.out.println("9) Search contacts");
            System.out.println("10) Filter by tag");
            System.out.println("11) Filter by date range");
            System.out.println("12) Filter by tag AND date range");
            System.out.println("13) Add multiple tags to a single contact");  // UC-12
            System.out.println("14) Remove a tag from a single contact");      // UC-12
            System.out.println("15) Exit");

            System.out.print("Enter choice (1-15): ");
            String choice = sc.nextLine().trim();

            if ("1".equals(choice)) {
                // === UC-11 Add Contact ===
                System.out.println("\n-- Add Contact --");

                System.out.println("Choose contact type:");
                System.out.println("1) Person");
                System.out.println("2) Organization");
                System.out.print("Enter 1 or 2: ");
                String typeChoice = sc.nextLine().trim();
                String typeTag = "person";
                if ("2".equals(typeChoice)) typeTag = "organization";

                System.out.print("Contact name: ");
                String name = sc.nextLine();

                List<String> phones = new ArrayList<>();
                System.out.print("Phone number: ");
                String p = sc.nextLine().trim();
                if (!p.isEmpty()) phones.add(p);

                List<String> emails = new ArrayList<>();
                System.out.print("Email address: ");
                String e = sc.nextLine().trim();
                if (!e.isEmpty()) emails.add(e);

                try {
                    Contact c = contactService.addContact(user, name, phones, emails, typeTag);
                    System.out.println("Contact added! ID: " + c.getId() + " | Type Tag: " + typeTag);
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if ("2".equals(choice)) {
                // === Edit contact (UC-05) ===
                System.out.println("\n-- Edit Contact --");
                System.out.print("Contact ID: ");
                String cid = sc.nextLine().trim();

                EditContactService edit = new EditContactService();

                System.out.print("New name (leave empty): ");
                String n = sc.nextLine();
                if (!n.trim().isEmpty()) edit.newName = n.trim();

                System.out.print("Replace phones? (y/n): ");
                if ("y".equalsIgnoreCase(sc.nextLine().trim())) {
                    List<String> newPhones = new ArrayList<>();
                    System.out.print("Phone 1: ");
                    String p1 = sc.nextLine().trim();
                    if (!p1.isEmpty()) newPhones.add(p1);
                    edit.newPhones = newPhones;
                }

                System.out.print("Replace emails? (y/n): ");
                if ("y".equalsIgnoreCase(sc.nextLine().trim())) {
                    List<String> newEmails = new ArrayList<>();
                    System.out.print("Email 1: ");
                    String e1 = sc.nextLine().trim();
                    if (!e1.isEmpty()) newEmails.add(e1);
                    edit.newEmails = newEmails;
                }

                try {
                    Contact updated = contactService.editContact(user, cid, edit);
                    System.out.println("Updated: " + updated.getName());
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if ("3".equals(choice)) {
                // === List contacts ===
                printContacts(contactRepo.getAll(user.getId()));

            } else if ("4".equals(choice)) {
                // === Delete single (UC-07) ===
                System.out.print("Enter ID to delete: ");
                String cid = sc.nextLine().trim();
                try {
                    contactService.deleteContact(user, cid);
                    System.out.println("Deleted.");
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if ("5".equals(choice)) {
                // === Bulk delete (UC-08) ===
                System.out.println("Enter comma-separated IDs:");
                List<String> ids = parseIds(sc.nextLine());
                System.out.println("Deleted " + contactService.bulkDelete(user, ids));

            } else if ("6".equals(choice)) {
                // === Bulk add tag (UC-08) ===
                System.out.println("Enter IDs:");
                List<String> ids = parseIds(sc.nextLine());
                System.out.print("Tag to add: ");
                String tag = sc.nextLine().trim();
                System.out.println("Added to " + contactService.bulkAddTag(user, ids, tag));

            } else if ("7".equals(choice)) {
                // === Bulk remove tag (UC-08) ===
                System.out.println("Enter IDs:");
                List<String> ids = parseIds(sc.nextLine());
                System.out.print("Tag to remove: ");
                String tag = sc.nextLine().trim();
                System.out.println("Removed from " + contactService.bulkRemoveTag(user, ids, tag));

            } else if ("8".equals(choice)) {
                // === User detail changes (UC-03) ===
                System.out.print("New name: ");
                String nn = sc.nextLine();
                System.out.print("New email: ");
                String ne = sc.nextLine();
                try {
                    profileService.updateProfile(user, nn, ne);
                    System.out.println("Updated.");
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if ("9".equals(choice)) {
                // === Search (UC-09) ===
                System.out.println("\nSearch by:");
                System.out.println("1) Name");
                System.out.println("2) Phone");
                System.out.println("3) Email");
                System.out.println("4) Tag");
                System.out.println("5) Anywhere");
                String f = sc.nextLine().trim();
                System.out.print("Text: ");
                String q = sc.nextLine().trim();

                List<Contact> results = new ArrayList<>();
                if ("1".equals(f)) results = contactService.searchByName(user, q);
                else if ("2".equals(f)) results = contactService.searchByPhone(user, q);
                else if ("3".equals(f)) results = contactService.searchByEmail(user, q);
                else if ("4".equals(f)) results = contactService.searchByTag(user, q);
                else if ("5".equals(f)) results = contactService.searchAll(user, q);
                else System.out.println("Invalid field.");

                printFilterResults(results);

            } else if ("10".equals(choice)) {
                // === Filter by tag (UC-10) ===
                System.out.print("Tag text (contains): ");
                printFilterResults(contactService.filterByTag(user, sc.nextLine().trim()));

            } else if ("11".equals(choice)) {
                // === Filter by date range (UC-10) ===
                System.out.print("Start date (yyyy-MM-dd): ");
                LocalDate start = DateUtils.parseDateOrNull(sc.nextLine());
                System.out.print("End date (yyyy-MM-dd): ");
                LocalDate end = DateUtils.parseDateOrNull(sc.nextLine());
                printFilterResults(contactService.filterByDate(user, start, end));

            } else if ("12".equals(choice)) {
                // === Filter by tag & date (UC-10) ===
                System.out.print("Tag text: ");
                String tagPart = sc.nextLine().trim();
                System.out.print("Start date: ");
                LocalDate start = DateUtils.parseDateOrNull(sc.nextLine());
                System.out.print("End date: ");
                LocalDate end = DateUtils.parseDateOrNull(sc.nextLine());
                printFilterResults(contactService.filterByTagAndDate(user, tagPart, start, end));

            } else if ("13".equals(choice)) {
                // === UC-12 Add multiple tags ===
                System.out.print("Enter Contact ID: ");
                String cid = sc.nextLine().trim();

                System.out.println("Enter tags separated by commas:");
                List<String> newTags = parseIds(sc.nextLine());  // reuse simple parser

                try {
                    contactService.addTagsToContact(user, cid, newTags);
                    System.out.println("Tags added.");
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if ("14".equals(choice)) {
                // === UC-12 Remove one tag ===
                System.out.print("Enter Contact ID: ");
                String cid = sc.nextLine().trim();
                System.out.print("Tag to remove: ");
                String tag = sc.nextLine().trim();

                try {
                    contactService.removeTagFromContact(user, cid, tag);
                    System.out.println("Tag removed.");
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if ("15".equals(choice)) {
                break;

            } else {
                System.out.println("Invalid choice. Please enter 1-15.");
            }
        }

        sc.close();
    }

    // Utility
    private static List<String> parseIds(String csv) {
        List<String> ids = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return ids;
        for (String p : csv.split(",")) {
            if (!p.trim().isEmpty()) ids.add(p.trim());
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
            System.out.println("No contacts matched.");
        } else {
            System.out.println("Matches: " + results.size());
            printContacts(results);
        }
    }
}
