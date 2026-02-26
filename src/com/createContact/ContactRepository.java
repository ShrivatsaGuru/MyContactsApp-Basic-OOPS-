package com.createContact;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
Contacts App : UC-04 Create Contact
This class stores contacts in memory.
It does the following things:
    - Uses a HashMap of userId → list of contacts
    - Adds contacts for users
    - Returns all contacts for a user
    - Counts contacts for a user
    - Keeps everything extremely simple for beginners

@author
Developer
@version 4.0
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
}