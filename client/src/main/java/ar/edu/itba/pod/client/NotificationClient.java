package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NotificationClient {
    private static Logger logger = LoggerFactory.getLogger(NotificationClient.class);

    public static void main(String[] args) throws InterruptedException, IOException, PropertyException {
        var properties = new NotificationClientProperties(new PropertyManager(System.getProperties()));
        var manager = new ClientRunner<>(properties);
        manager.run();
    }
}
