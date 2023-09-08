package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NotificationClient implements Client<NotificationClientProperties> {
	private static Logger logger = LoggerFactory.getLogger(NotificationClient.class);

	public static void main(String[] args) throws InterruptedException, IOException, PropertyException {
		var properties = new NotificationClientProperties(new PropertyManager(System.getProperties()));
		var manager = new ClientManager<>(new NotificationClient(), properties);
		manager.run();
	}

	@Override
	public void run(NotificationClientProperties properties) {
		// TODO: Implement client instead of logging
		logger.info("properties: {}", properties.toString());
	}
}
