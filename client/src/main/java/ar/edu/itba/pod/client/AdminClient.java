package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AdminClient {
    private static Logger logger = LoggerFactory.getLogger(AdminClient.class);

    public static void main(String[] args) throws InterruptedException, IOException, PropertyException {
        var properties = new AdminClientProperties(new PropertyManager(System.getProperties()));
        var manager = new ClientRunner<>(properties);
        manager.run();
    }
}
