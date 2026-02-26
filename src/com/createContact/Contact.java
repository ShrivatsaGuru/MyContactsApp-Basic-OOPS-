package com.createContact;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
Contacts App : UC-04 Create Contact
This class represents a simple contact.
It does the following things:
    - Stores contact id, owner user id, and name
    - Stores phone numbers and email addresses in lists
    - Allows adding phones and emails easily
    - Uses UUID for a unique contact ID
    - Keeps the design very simple for beginners

@author Developer
@version 4.0
*/

public class Contact {

    private String id;
    private String ownerUserId;
    private String name;
    private LocalDateTime createdAt;

    private List<String> phoneNumbers = new ArrayList<>();
    private List<String> emailAddresses = new ArrayList<>();

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

    public void addPhone(String number) {
        phoneNumbers.add(number);
    }

    public void addEmail(String email) {
        emailAddresses.add(email);
    }
}
