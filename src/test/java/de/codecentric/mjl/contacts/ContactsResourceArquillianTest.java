package de.codecentric.mjl.contacts;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * In diesem Test werden alle Komponenten der echten Anwendung verwendet (keine Mocks).
 * Das heißt, dass auch das echte ContactRepository verwendet wird und (falls dieses wirklich auf die Datenbank
 * zugreifen würde - was hier nicht der Fall ist) eine laufende Datenbank vorhanden sein muss.
 */
@RunWith(Arquillian.class)
public class ContactsResourceArquillianTest {

    public static final GenericType<List<Contact>> CONTACT_LIST_TYPE = new GenericType<List<Contact>>() {
    };

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(Contact.class, ContactsResource.class, ContactRepository.class, RESTConfiguration.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    ContactsResource contactsResource;

    /**
     * Dieser Test läuft direkt im Container, deshalb kann auf alle Beans direkt zugegriffen werden.
     */
    @Test
    public void testGetContacts() throws Exception {
        // this test is executed directly on the server, so we can access contactsResource directly
        List<Contact> contacts = contactsResource.getContacts();
        assertContactList(contacts);
    }


    /**
     * Dieser Test läuft außerhalb des Containers. Wir können nur über die REST-Schnittstelle zugreifen.
     */
    @RunAsClient
    @Test
    public void testGetContactsAsClient(@ArquillianResource URL deploymentURL) throws Exception {
        WebTarget target = buildWebTarget(deploymentURL);
        List<Contact> contacts = target.path("rest/contacts")
                .request(MediaType.APPLICATION_JSON)
                .get(CONTACT_LIST_TYPE);
        assertContactList(contacts);

        // contactsResource is empty here, because we are in the contacts-client
        assertNull(contactsResource);
    }

    private static WebTarget buildWebTarget(URL deploymentURL) throws URISyntaxException {
        Client client = ClientBuilder.newClient();
        return client.target(deploymentURL.toURI());
    }

    private void assertContactList(List<Contact> contacts) {
        assertThat(contacts, contains(equalTo(new Contact("Michael Lex", "michael.lex@localhost.local"))));
    }

}
