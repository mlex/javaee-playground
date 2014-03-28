package de.codecentric.mjl.contacts;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * In diesem Test wird das ContactRepository durch einen Mock ersetzt.
 *
 * Dafür muss die Mockito-Abhängigkeit mit in das ShrinkWrap-WAR gepackt werden.
 */
@RunWith(Arquillian.class)
public class ContactsResourceArquillianWithMockTest {

    public static final GenericType<List<Contact>> CONTACT_LIST_TYPE = new GenericType<List<Contact>>() {
    };

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(Contact.class, ContactsResource.class, ContactRepository.class,
                        ContactRepositoryMockProvider.class, RESTConfiguration.class)
                .addAsLibraries(Maven.resolver().resolve("org.mockito:mockito-all:1.9.0").withTransitivity().asFile())
                .addAsWebInfResource("beans.xml");
    }

    @Inject
    ContactRepository contactRepository;

    @Inject
    ContactsResource contactsResource;

    @Test
    public void testGetContacts() throws Exception {
        when(contactRepository.getContacts()).thenReturn(Arrays.asList(new Contact("Foo Bar", "foobar@localhost.local")));
        // this test is executed directly on the server, so we can access contactsResource and -Repository directly
        List<Contact> contacts = contactsResource.getContacts();
        assertContactList(contacts);
    }

    @RunAsClient
    @Test
    public void testGetContactsAsClient(@ArquillianResource URL deploymentURL) throws Exception {
        // on the client side, the beans are not injected!
        assertNull(contactsResource);
        assertNull(contactRepository);

        // So we can't configure our Mocks!
        WebTarget target = buildWebTarget(deploymentURL);
        List<Contact> contacts = target.path("rest/contacts")
                .request(MediaType.APPLICATION_JSON)
                .get(CONTACT_LIST_TYPE);
        assertThat(contacts, hasSize(0));
    }

    private static WebTarget buildWebTarget(URL deploymentURL) throws URISyntaxException {
        Client client = ClientBuilder.newClient();
        return client.target(deploymentURL.toURI());
    }

    private void assertContactList(List<Contact> contacts) {
        assertThat(contacts, contains(equalTo(new Contact("Foo Bar", "foobar@localhost.local"))));
    }

}
