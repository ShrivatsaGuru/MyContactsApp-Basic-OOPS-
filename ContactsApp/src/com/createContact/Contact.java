package com.createContact;


import java.time.LocalDateTime;
import java.util.*;


/*
Contacts App : UC-08 Bulk Operations
This class represents a simple contact.
It does the following things:
    - Stores contact id, owner user id, and name
    - Stores phone numbers and email addresses in lists
    - Supports simple tagging with a list of tags
    - Allows adding/removing tags in a very basic way
    - Keeps the design very simple for beginners

@author Developer
@version 8.0
*/

public class Contact {

    private String id;
    private String ownerUserId;
    private String name;
    private LocalDateTime createdAt;

    private List<String> phoneNumbers = new ArrayList<>();
    private List<String> emailAddresses = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    public Contact(String ownerUserId, String name) {
        this.id = UUID.randomUUID().toString();
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getOwnerUserId() { return ownerUserId; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<String> getPhoneNumbers() { return phoneNumbers; }
    public List<String> getEmailAddresses() { return emailAddresses; }
    public List<String> getTags() { return tags; }

    public void addPhone(String number) { phoneNumbers.add(number); }
    public void addEmail(String email) { emailAddresses.add(email); }

    // Edits from UC-05 (kept)
    public void setName(String newName) { this.name = newName; }
    public void setPhoneNumbers(List<String> newPhones) {
        phoneNumbers.clear();
        if (newPhones != null) phoneNumbers.addAll(newPhones);
    }
    public void setEmailAddresses(List<String> newEmails) {
        emailAddresses.clear();
        if (newEmails != null) emailAddresses.addAll(newEmails);
    }

    // Simple tagging helpers for UC-08
    public void addTag(String tag) {
        if (tag == null) return;
        String t = tag.trim();
        if (t.isEmpty()) return;
        if (!tags.contains(t)) tags.add(t);
    }

    public void removeTag(String tag) {
        if (tag == null) return;
        tags.remove(tag.trim());
    }
}
