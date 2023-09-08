package ar.edu.itba.pod.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyNotFoundException;

import java.io.IOException;

abstract public class BaseClientProperties {
    private static Logger logger = LoggerFactory.getLogger(BaseClientProperties.class);

    private String serverAddress;

    public BaseClientProperties(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public BaseClientProperties(PropertyManager properties) throws PropertyNotFoundException {
        this(properties.getProperty("serverAddress"));
    }

    public String getServerAddress() {
        return serverAddress;
    }

    abstract public ClientAction getAction();

    public void runAction() throws IOException {
        ClientAction action = getAction();
        logger.info("Running action: {}", action);

        action.run();
    }
}
