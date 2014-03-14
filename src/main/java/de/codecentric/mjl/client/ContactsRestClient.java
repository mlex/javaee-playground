package de.codecentric.mjl.client;

import de.codecentric.mjl.contacts.Contact;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class ContactsRestClient {

    public static final GenericType<List<Contact>> CONTACT_LIST_TYPE = new GenericType<List<Contact>>() {
    };

    private final String baseUrl;
    private final Client client;

    public ContactsRestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        client = ClientBuilder.newClient();
    }

    public List<Contact> getContactsFromRemoteServer() {
        return getTarget().path("contacts/contacts").request(MediaType.APPLICATION_JSON_TYPE).get(CONTACT_LIST_TYPE);
    }

    private WebTarget getTarget() {
        return client.target(baseUrl);
    }

}
