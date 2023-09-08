package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyNotFoundException;

abstract public class BaseClientProperties {
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

	abstract public Action getAction();
}
