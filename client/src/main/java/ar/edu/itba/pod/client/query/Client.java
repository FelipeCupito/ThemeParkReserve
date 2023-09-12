package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.ClientRunner;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException, IOException, PropertyException {
        var properties = new ClientProperties(new PropertyManager(System.getProperties()));
        var manager = new ClientRunner<>(properties);
        manager.run();
    }
}
