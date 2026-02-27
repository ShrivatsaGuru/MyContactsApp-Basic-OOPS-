package com.createContact;


import java.util.*;

/*
Contacts App : UC-07 Delete Contact
This class stores contacts in memory.
It does the following things:
    - Uses a HashMap of userId → list of contacts
    - Adds and lists contacts for a user
    - Looks up a contact by its id
    - Deletes a contact by its id for a user
    - Keeps everything very simple for beginners

@author Developer
@version 7.0
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

    // NEW in UC-07: delete a contact by ID for the given user
    public boolean deleteById(String userId, String contactId) {
        List<Contact> list = byUser.get(userId);
        if (list == null) return false;

        Iterator<Contact> it = list.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c.getId().equals(contactId)) {
                it.remove();
                return true;
            }
        }
        return false;
    }
}