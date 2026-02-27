package com.createContact;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.editContact.EditContactService;
import com.exception.*;

import java.util.*;

/*
Contacts App : UC-06 Edit Contact
This class edits existing contacts.
It does the following things:
    - Finds a contact by id for the current user
    - Updates name if provided
    - Replaces phone numbers if provided
    - Replaces email addresses if provided
    - Keeps logic very small and easy to read

@author Developer
@version 6.0
*/

public class ContactService {

    private ContactRepository repo;

    public ContactService(ContactRepository repo) {
        this.repo = repo;
    }

    // From UC-04 (kept for completeness)
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

    // --- New in UC-05: Edit an existing contact ---
    public Contact editContact(User owner, String contactId, EditContactService edit) {
        if (contactId == null || contactId.trim().isEmpty()) {
            throw new ValidationException("Contact ID cannot be empty");
        }

        Contact c = repo.getById(owner.getId(), contactId);
        if (c == null) {
            throw new ValidationException("Contact not found for this user");
        }

        if (edit.newName != null && !edit.newName.trim().isEmpty()) {
            c.setName(edit.newName.trim());
        }
        if (edit.newPhones != null) {
            c.setPhoneNumbers(edit.newPhones);
        }
        if (edit.newEmails != null) {
            c.setEmailAddresses(edit.newEmails);
        }

        return c;
    }
}
