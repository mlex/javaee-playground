package de.codecentric.mjl.hello;

import javax.enterprise.event.Observes;
import java.util.logging.Logger;

public class SimpleListener {

    private static final Logger LOGGER = Logger.getLogger(SimpleListener.class.getName());


    public void reactOnMessageChangedEvent(@Observes MessageChangedEvent event) {
        LOGGER.info("RECEIVED MessageChangedEvent");
    }
}
