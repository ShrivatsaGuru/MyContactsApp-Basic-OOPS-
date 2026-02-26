package com.createContact;
import com.main.*;
import com.registration.*;
import com.user.*;
import com.user.validation.*;
import com.exception.*;

import java.util.List;

/*
Contacts App : UC-04 Create Contact
This class creates new contacts.
It does the following things:
    - Checks Free and Premium contact limits
    - Creates a contact with name, phones, and emails
    - Stores the contact in the repository
    - Makes everything very small and beginner-friendly

@author
Developer
@version 4.0
*/

public class ContactService {

    private ContactRepository repo;

    public ContactService(ContactRepository repo) {
        this.repo = repo;
    }

    public Contact addContact(User owner,
                              String name,
                              List<String> phones,
                              List<String> emails) {

        int current = repo.countForUser(owner.getId());
        int limit = owner.getUserType().getMaxContacts();

        if (current >= limit) {
            throw new ValidationException("Contact limit reached for user type " + owner.getUserType());
        }

        Contact c = new Contact(owner.getId(), name);

        if (phones != null) {
            for (String p : phones) c.addPhone(p);
        }

        if (emails != null) {
            for (String e : emails) c.addEmail(e);
        }

        repo.add(owner.getId(), c);
        return c;
    }
}
