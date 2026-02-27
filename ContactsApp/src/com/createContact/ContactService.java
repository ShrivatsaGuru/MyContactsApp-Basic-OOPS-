package com.createContact;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.editContact.EditContactService;
import com.exception.*;

import java.util.*;



/*
Contacts App : UC-09 Search Contacts
This class manages contacts for a user.
It does the following things:
    - Adds and edits contacts (from earlier UCs)
    - Searches contacts by name, phone, email, or tags
    - Searches across all fields with a single query
    - Uses simple "contains" matching (case-insensitive)
    - Keeps logic small and beginner-friendly

@author Developer
@version 9.0
*/

public class ContactService {

    private ContactRepository repo;

    public ContactService(ContactRepository repo) {
        this.repo = repo;
    }

    // --- Existing methods kept from earlier UCs (add/edit/delete) ---
    public Contact addContact(User owner, String name, List<String> phones, List<String> emails) {
        int current = repo.countForUser(owner.getId());
        int limit = owner.getUserType().getMaxContacts();
        if (current >= limit) {
            throw new ValidationException("Contact limit reached for user type " + owner.getUserType());
        }
        Contact c = new Contact(owner.getId(), name);
        if (phones != null) for (String p : phones) c.addPhone(p);
        if (emails != null) for (String e : emails) c.addEmail(e);
        repo.add(owner.getId(), c);
        return c;
    }

    public Contact editContact(User owner, String contactId, EditContactService edit) {
        if (contactId == null || contactId.trim().isEmpty()) {
            throw new ValidationException("Contact ID cannot be empty");
        }
        Contact c = repo.getById(owner.getId(), contactId);
        if (c == null) throw new ValidationException("Contact not found for this user");
        if (edit.newName != null && !edit.newName.trim().isEmpty()) c.setName(edit.newName.trim());
        if (edit.newPhones != null) c.setPhoneNumbers(edit.newPhones);
        if (edit.newEmails != null) c.setEmailAddresses(edit.newEmails);
        return c;
    }

    public void deleteContact(User owner, String contactId) {
        if (contactId == null || contactId.trim().isEmpty()) {
            throw new ValidationException("Contact ID cannot be empty");
        }
        boolean removed = repo.deleteById(owner.getId(), contactId);
        if (!removed) throw new ValidationException("Contact not found or already deleted");
    }

    public int bulkDelete(User owner, List<String> contactIds) {
        if (contactIds == null || contactIds.isEmpty()) return 0;
        int deleted = 0;
        for (String id : contactIds) {
            if (id == null || id.trim().isEmpty()) continue;
            if (repo.deleteById(owner.getId(), id.trim())) deleted++;
        }
        return deleted;
    }

    public int bulkAddTag(User owner, List<String> contactIds, String tag) {
        if (contactIds == null || contactIds.isEmpty()) return 0;
        if (tag == null || tag.trim().isEmpty()) throw new ValidationException("Tag cannot be empty");
        int updated = 0;
        for (String id : contactIds) {
            if (id == null || id.trim().isEmpty()) continue;
            Contact c = repo.getById(owner.getId(), id.trim());
            if (c != null) {
                c.addTag(tag.trim());
                updated++;
            }
        }
        return updated;
    }

    public int bulkRemoveTag(User owner, List<String> contactIds, String tag) {
        if (contactIds == null || contactIds.isEmpty()) return 0;
        if (tag == null || tag.trim().isEmpty()) throw new ValidationException("Tag cannot be empty");
        int updated = 0;
        for (String id : contactIds) {
            if (id == null || id.trim().isEmpty()) continue;
            Contact c = repo.getById(owner.getId(), id.trim());
            if (c != null) {
                c.removeTag(tag.trim());
                updated++;
            }
        }
        return updated;
    }

    // --- NEW in UC-09: Simple, case-insensitive "contains" searches ---

    public List<Contact> searchByName(User owner, String text) {
        List<Contact> all = repo.getAll(owner.getId());
        List<Contact> out = new ArrayList<>();
        if (isBlank(text)) return out;
        for (Contact c : all) {
            if (containsIgnoreCase(c.getName(), text)) out.add(c);
        }
        return out;
    }

    public List<Contact> searchByPhone(User owner, String digits) {
        List<Contact> all = repo.getAll(owner.getId());
        List<Contact> out = new ArrayList<>();
        if (isBlank(digits)) return out;
        for (Contact c : all) {
            for (String p : c.getPhoneNumbers()) {
                if (containsIgnoreCase(p, digits)) { out.add(c); break; }
            }
        }
        return out;
    }

    public List<Contact> searchByEmail(User owner, String text) {
        List<Contact> all = repo.getAll(owner.getId());
        List<Contact> out = new ArrayList<>();
        if (isBlank(text)) return out;
        for (Contact c : all) {
            for (String e : c.getEmailAddresses()) {
                if (containsIgnoreCase(e, text)) { out.add(c); break; }
            }
        }
        return out;
    }

    public List<Contact> searchByTag(User owner, String tagPart) {
        List<Contact> all = repo.getAll(owner.getId());
        List<Contact> out = new ArrayList<>();
        if (isBlank(tagPart)) return out;
        for (Contact c : all) {
            for (String t : c.getTags()) {
                if (containsIgnoreCase(t, tagPart)) { out.add(c); break; }
            }
        }
        return out;
    }

    // One query across name, phones, emails, and tags
    public List<Contact> searchAll(User owner, String query) {
        Set<String> seenIds = new HashSet<>();
        List<Contact> result = new ArrayList<>();
        if (isBlank(query)) return result;

        for (Contact c : repo.getAll(owner.getId())) {
            boolean match = false;

            if (containsIgnoreCase(c.getName(), query)) match = true;

            if (!match) {
                for (String p : c.getPhoneNumbers())
                    if (containsIgnoreCase(p, query)) { match = true; break; }
            }

            if (!match) {
                for (String e : c.getEmailAddresses())
                    if (containsIgnoreCase(e, query)) { match = true; break; }
            }

            if (!match) {
                for (String t : c.getTags())
                    if (containsIgnoreCase(t, query)) { match = true; break; }
            }

            if (match && !seenIds.contains(c.getId())) {
                seenIds.add(c.getId());
                result.add(c);
            }
        }
        return result;
    }

    // --- Helpers ---
    private static boolean containsIgnoreCase(String text, String part) {
        if (text == null || part == null) return false;
        return text.toLowerCase().contains(part.toLowerCase());
        }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}