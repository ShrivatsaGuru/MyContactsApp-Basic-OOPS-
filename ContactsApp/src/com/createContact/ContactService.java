package com.createContact;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.editContact.EditContactService;
import com.exception.*;

import java.util.*;


/*
Contacts App : UC-07 Delete Contact
This class manages contacts for a user.
It does the following things:
    - Adds new contacts (from earlier UCs)
    - Edits existing contacts (from UC-05)
    - Deletes a contact by its ID (new in UC-07)
    - Keeps logic very small and easy to follow

@author Developer
@version 7.0
*/

public class ContactService {

    private ContactRepository repo;

    public ContactService(ContactRepository repo) {
        this.repo = repo;
    }

    // From UC-04: add a contact (kept here for completeness)
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

    // From UC-05: edit a contact (kept here for completeness)
    public Contact editContact(User owner, String contactId, EditContactService edit) {
        if (contactId == null || contactId.trim().isEmpty()) {
            throw new ValidationException("Contact ID cannot be empty");
        }
        Contact c = repo.getById(owner.getId(), contactId);
        if (c == null) {
            throw new ValidationException("Contact not found for this user");
        }
        if (edit.newName != null && !edit.newName.trim().isEmpty()) c.setName(edit.newName.trim());
        if (edit.newPhones != null) c.setPhoneNumbers(edit.newPhones);
        if (edit.newEmails != null) c.setEmailAddresses(edit.newEmails);
        return c;
    }

    // NEW in UC-07: delete a contact by id
    public void deleteContact(User owner, String contactId) {
        if (contactId == null || contactId.trim().isEmpty()) {
            throw new ValidationException("Contact ID cannot be empty");
        }
        boolean removed = repo.deleteById(owner.getId(), contactId);
        if (!removed) {
            throw new ValidationException("Contact not found or already deleted");
        }
    }
}