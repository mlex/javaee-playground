package de.codecentric.mjl.hello;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
@HelloSource(HelloSource.Source.SESSION)
public class SessionHelloService implements Serializable, HelloService {

    private String helloMessage = "Hello from the Service";

    public SessionHelloService() {
    }

    @Override
    public String getHelloMessage() {
        return helloMessage;
    }

    @Override
    public void setHelloMessage(String helloMessage) {
        this.helloMessage = helloMessage;
    }
}