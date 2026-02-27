package com.createContact;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.editContact.EditContactService;
import com.exception.*;

import java.util.*;


/*
Contacts App : UC-08 Bulk Operations
This class manages contacts for a user.
It does the following things:
    - Adds new contacts (from earlier UCs)
    - Edits existing contacts (from UC-05)
    - Deletes many contacts at once (bulk delete)
    - Adds or removes a tag on many contacts at once (bulk tag)
    - Keeps logic very small and easy to follow for beginners

@author Developer
@version 8.0
*/

public class ContactService {

    private ContactRepository repo;

    public ContactService(ContactRepository repo) {
        this.repo = repo;
    }

    // From UC-04 (kept)
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

    // From UC-05 (kept)
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

    // From UC-07 (kept)
    public void deleteContact(User owner, String contactId) {
        if (contactId == null || contactId.trim().isEmpty()) {
            throw new ValidationException("Contact ID cannot be empty");
        }
        boolean removed = repo.deleteById(owner.getId(), contactId);
        if (!removed) throw new ValidationException("Contact not found or already deleted");
    }

    // --- New in UC-08: Bulk delete many contacts by IDs ---
    public int bulkDelete(User owner, List<String> contactIds) {
        if (contactIds == null || contactIds.isEmpty()) return 0;
        int deleted = 0;
        for (String id : contactIds) {
            if (id == null || id.trim().isEmpty()) continue;
            if (repo.deleteById(owner.getId(), id.trim())) deleted++;
        }
        return deleted;
    }

    // --- New in UC-08: Bulk add the same tag to many contacts ---
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

    // --- New in UC-08: Bulk remove the same tag from many contacts ---
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
}