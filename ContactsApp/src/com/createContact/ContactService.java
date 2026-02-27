package com.createContact;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.editContact.EditContactService;
import com.exception.*;

import java.util.*;


import java.time.LocalDate;

/*
Contacts App : UC-11 Filter Contacts
This class manages and filters contacts.
It does the following things:
    - Adds, edits, deletes, tags (from earlier UCs)
    - (UC-09) Searches by name, phone, email, tag, or everywhere
    - (UC-10) Filters by tag, by date range, or by tag AND date range
    - Uses simple "contains" matching (case-insensitive)
    - Keeps logic very simple and easy to read
    - Lets user add custom tags to contacts

@author Developer
@version 11.0
*/

public class ContactService {

    private ContactRepository repo;

    public ContactService(ContactRepository repo) {
        this.repo = repo;
    }

    // ===== Core operations (kept from earlier UCs) =====

 // Inside ContactService class
    public Contact addContact(User owner, String name, List<String> phones, List<String> emails, String contactTypeTag) {

        int current = repo.countForUser(owner.getId());
        int limit = owner.getUserType().getMaxContacts();
        if (current >= limit) {
            throw new ValidationException("Contact limit reached for user type " + owner.getUserType());
        }

        Contact c = new Contact(owner.getId(), name);

        // Basic fields
        if (phones != null) for (String p : phones) c.addPhone(p);
        if (emails != null) for (String e : emails) c.addEmail(e);

        // === NEW IN UC-11: assign the type tag ===
        c.addTag(contactTypeTag);

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

    // ===== UC-09: Search (KEPT as-is) =====

    public List<Contact> searchByName(User owner, String text) {
        List<Contact> out = new ArrayList<>();
        if (isBlank(text)) return out;
        for (Contact c : repo.getAll(owner.getId())) {
            if (containsIgnoreCase(c.getName(), text)) out.add(c);
        }
        return out;
    }

    public List<Contact> searchByPhone(User owner, String digits) {
        List<Contact> out = new ArrayList<>();
        if (isBlank(digits)) return out;
        for (Contact c : repo.getAll(owner.getId())) {
            for (String p : c.getPhoneNumbers()) {
                if (containsIgnoreCase(p, digits)) { out.add(c); break; }
            }
        }
        return out;
    }

    public List<Contact> searchByEmail(User owner, String text) {
        List<Contact> out = new ArrayList<>();
        if (isBlank(text)) return out;
        for (Contact c : repo.getAll(owner.getId())) {
            for (String e : c.getEmailAddresses()) {
                if (containsIgnoreCase(e, text)) { out.add(c); break; }
            }
        }
        return out;
    }

    public List<Contact> searchByTag(User owner, String tagPart) {
        List<Contact> out = new ArrayList<>();
        if (isBlank(tagPart)) return out;
        for (Contact c : repo.getAll(owner.getId())) {
            for (String t : c.getTags()) {
                if (containsIgnoreCase(t, tagPart)) { out.add(c); break; }
            }
        }
        return out;
    }

    public List<Contact> searchAll(User owner, String query) {
        Set<String> seen = new HashSet<>();
        List<Contact> result = new ArrayList<>();
        if (isBlank(query)) return result;

        for (Contact c : repo.getAll(owner.getId())) {
            boolean match = false;

            if (containsIgnoreCase(c.getName(), query)) match = true;

            if (!match) for (String p : c.getPhoneNumbers())
                if (containsIgnoreCase(p, query)) { match = true; break; }

            if (!match) for (String e : c.getEmailAddresses())
                if (containsIgnoreCase(e, query)) { match = true; break; }

            if (!match) for (String t : c.getTags())
                if (containsIgnoreCase(t, query)) { match = true; break; }

            if (match && seen.add(c.getId())) result.add(c);
        }
        return result;
    }

    // ===== UC-10: Filter (NEW) =====

    public List<Contact> filterByTag(User owner, String tagPart) {
        List<Contact> out = new ArrayList<>();
        if (isBlank(tagPart)) return out;
        String needle = tagPart.toLowerCase();
        for (Contact c : repo.getAll(owner.getId())) {
            for (String t : c.getTags()) {
                if (t != null && t.toLowerCase().contains(needle)) {
                    out.add(c); break;
                }
            }
        }
        return out;
    }

    public List<Contact> filterByDate(User owner, LocalDate start, LocalDate end) {
        List<Contact> out = new ArrayList<>();
        for (Contact c : repo.getAll(owner.getId())) {
            LocalDate created = c.getCreatedAt().toLocalDate();
            boolean ok = true;
            if (start != null && created.isBefore(start)) ok = false;
            if (end   != null && created.isAfter(end))   ok = false;
            if (ok) out.add(c);
        }
        return out;
    }

    public List<Contact> filterByTagAndDate(User owner, String tagPart, LocalDate start, LocalDate end) {
        List<Contact> byTag = filterByTag(owner, tagPart);
        if (start == null && end == null) return byTag;

        List<Contact> out = new ArrayList<>();
        for (Contact c : byTag) {
            LocalDate created = c.getCreatedAt().toLocalDate();
            boolean ok = true;
            if (start != null && created.isBefore(start)) ok = false;
            if (end   != null && created.isAfter(end))   ok = false;
            if (ok) out.add(c);
        }
        return out;
    }

    // Helpers
    private static boolean containsIgnoreCase(String text, String part) {
        if (text == null || part == null) return false;
        return text.toLowerCase().contains(part.toLowerCase());
    }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}