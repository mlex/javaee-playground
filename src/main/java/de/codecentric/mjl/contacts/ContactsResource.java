package de.codecentric.mjl.contacts;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@RequestScoped
@Path("/contacts")
public class ContactsResource {

    @Inject
    private ContactRepository contactRepository;

    @GET
    @Produces("application/json")
    public List<Contact> getContacts() {
        return contactRepository.getContacts();
    }

}
