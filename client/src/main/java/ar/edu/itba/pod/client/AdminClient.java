package ar.edu.itba.pod.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;

public class AdminClient implements Client<AdminClientProperties> {
	private static Logger logger = LoggerFactory.getLogger(AdminClient.class);

	public static void main(String[] args) throws InterruptedException, IOException, PropertyException {
		var properties = new AdminClientProperties(new PropertyManager(System.getProperties()));
		var manager = new ClientManager<>(new AdminClient(), properties);
		manager.run();
	}

	@Override
	public void run(AdminClientProperties properties) {
		// TODO: Implement client instead of logging
		logger.info("properties: %s", properties.toString());
		var action = properties.getAction();
		if (action instanceof RidesAction) {
			logger.info(((RidesAction) action).getAttractions().collect(Collectors.toList()).toString());
		} else if (action instanceof TicketsAction) {
			logger.info(((TicketsAction) action).getPasses().collect(Collectors.toList()).toString());
		}
	}
}
