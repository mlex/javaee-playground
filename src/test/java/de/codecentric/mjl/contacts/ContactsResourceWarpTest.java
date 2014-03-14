package de.codecentric.mjl.contacts;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.jboss.arquillian.warp.servlet.BeforeServlet;
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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Mit Warp wird die Anwendung zwar von außen getestet (@RunAsClient), es ist aber trotzdem möglich vor oder nach
 * einzelnen Requests noch Aktionen/Verifikationen im Container durchzuführen.
 *
 * Wichtig ist, dass die URL für die Tests per @ArquillianResource injected wird.
 */
@WarpTest
@RunWith(Arquillian.class)
public class ContactsResourceWarpTest {

    public static final GenericType<List<Contact>> CONTACT_LIST_TYPE = new GenericType<List<Contact>>() {
    };

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(Contact.class, ContactsResource.class, ContactRepository.class, RESTConfiguration.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /**
     * You (seemingly) can't mix Warp-Client-Tests and Embedded tests ->
     * java.lang.ClassNotFoundException: de.codecentric.mjl.contacts.ContactsResourceWarpTest
     */
    /*
    @Inject
    ContactsResource contactsResource;

    @Test
    public void testGetContacts() throws Exception {
        List<Contact> contacts = contactsResource.getContacts();
        assertContactList(contacts);
    }
    */

    @RunAsClient
    @Test
    public void testGetContactsAsClient(@ArquillianResource URL deploymentURL) throws Exception {
        WebTarget target = buildWebTarget(deploymentURL);
        List<Contact> contacts = target.path("contacts/contacts")
                .request(MediaType.APPLICATION_JSON)
                .get(CONTACT_LIST_TYPE);
        assertContactList(contacts);
    }

    @RunAsClient
    @Test
    public void testGetContactsAsClientWithServerSideValidation(@ArquillianResource URL deploymentURL) throws URISyntaxException {
        final WebTarget webTarget = buildWebTarget(deploymentURL);
        Warp.initiate(new Activity() {
            @Override
            public void perform() {
                List<Contact> contacts = webTarget.path("contacts/contacts").request(MediaType.APPLICATION_JSON).get(CONTACT_LIST_TYPE);
                assertContactList(contacts);

            }
        }).inspect(new Inspection() {
            private static final long serialVersionUID = 1L;

            @ArquillianResource
            private RestContext restContext;

            // BeforeServlet can not access the RestContext -> leads to exception
            /*
            @BeforeServlet
            public void beforeServlet() {
                assertFalse(true);
            }
            */

            @AfterServlet
            public void afterServlet() {
                assertEquals(restContext.getHttpRequest().getHeaders().get("Accept").get(0), "application/json");
            }
        });
    }

    @RunAsClient
    @Test
    public void testGetContactsAsClientWithServerSidePreparation(@ArquillianResource URL deploymentURL) throws URISyntaxException {
        final WebTarget webTarget = buildWebTarget(deploymentURL);
        Warp.initiate(new Activity() {
            @Override
            public void perform() {
                List<Contact> contacts = webTarget.path("contacts/contacts").request(MediaType.APPLICATION_JSON).get(CONTACT_LIST_TYPE);
                assertThat(contacts, hasSize(2));
            }
        }).inspect(new Inspection() {
            private static final long serialVersionUID = 1L;

            @Inject
            private ContactRepository contactRepository;

            private transient List<Contact> tmp;

            @BeforeServlet
            public void beforeServlet() {
                tmp = new ArrayList<Contact>();
                tmp.addAll(contactRepository.getContacts());
                contactRepository.getContacts().clear();
                contactRepository.getContacts().add(new Contact());
                contactRepository.getContacts().add(new Contact());
            }

            @AfterServlet
            public void afterServlet() {
                contactRepository.getContacts().clear();
                contactRepository.getContacts().addAll(tmp);
            }
        });
    }

    private static WebTarget buildWebTarget(URL deploymentURL) throws URISyntaxException {
        Client client = ClientBuilder.newClient();
        return client.target(deploymentURL.toURI());
    }

    private void assertContactList(List<Contact> contacts) {
        assertThat(contacts, contains(equalTo(new Contact("Michael Lex", "michael.lex@localhost.local"))));
    }


}
