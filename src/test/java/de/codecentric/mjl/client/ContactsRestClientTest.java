package de.codecentric.mjl.client;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRule;
import de.codecentric.mjl.contacts.Contact;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class ContactsRestClientTest {

    @Rule
    public ClientDriverRule driver = new ClientDriverRule();

    private ContactsRestClient client = new ContactsRestClient(driver.getBaseUrl());

    @Test
    public void testGetContactsFromRemoteServer() {
        driver.addExpectation(
                onRequestTo("/contacts/contacts").withMethod(ClientDriverRequest.Method.GET),
                giveResponse("[{\"name\":\"test\",\"email\":\"test@foo.local\"}]", "application/json"));
        List<Contact> contacts = client.getContactsFromRemoteServer();
        assertThat(contacts, contains(new Contact("test", "test@foo.local")));
    }
}
