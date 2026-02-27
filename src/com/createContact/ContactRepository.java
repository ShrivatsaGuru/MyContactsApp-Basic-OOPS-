package com.createContact;


import java.util.*;

/*
Contacts App : UC-06 Edit Contact
This class stores contacts in memory.
It does the following things:
    - Uses a HashMap of userId → list of contacts
    - Adds contacts for users
    - Returns all contacts for a user
    - Looks up a specific contact by its id
    - Keeps everything extremely simple for beginners

@author Developer
@version 6.0
*/

public class ContactRepository {

    private HashMap<String, List<Contact>> byUser = new HashMap<>();

    public void add(String userId, Contact contact) {
        List<Contact> list = byUser.getOrDefault(userId, new ArrayList<>());
        list.add(contact);
        byUser.put(userId, list);
    }

    public List<Contact> getAll(String userId) {
        return byUser.getOrDefault(userId, new ArrayList<>());
    }

    public int countForUser(String userId) {
        return getAll(userId).size();
    }

    public Contact getById(String userId, String contactId) {
        for (Contact c : getAll(userId)) {
            if (c.getId().equals(contactId)) return c;
        }
        return null;
    }
}