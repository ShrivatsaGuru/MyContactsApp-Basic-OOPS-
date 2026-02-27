package com.editContact;

import java.util.List;

/*
Contacts App : UC-06 Edit Contact
This class carries new values for editing a contact.
It does the following things:
    - Holds optional new name
    - Holds optional new list of phone numbers
    - Holds optional new list of email addresses
    - Keeps editing inputs simple and clear for beginners

@author Developer
@version 6.0
*/

public class EditContactService 
{
    public String newName;            
    public List<String> newPhones;    
    public List<String> newEmails;    
}