package de.codecentric.mjl.contacts;

import org.mockito.Mockito;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

public class ContactRepositoryMockProvider {
    @Alternative
    @Produces
    @Singleton
    public ContactRepository contactRepository = Mockito.mock(ContactRepository.class);

}
