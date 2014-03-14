package de.codecentric.mjl.contacts;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ContactRepository {

    private List<Contact> contacts = new ArrayList<Contact>();

    public ContactRepository() {
        this.contacts.add(new Contact("Michael Lex", "michael.lex@localhost.local"));
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}
