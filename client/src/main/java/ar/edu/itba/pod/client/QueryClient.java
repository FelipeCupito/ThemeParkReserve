package ar.edu.itba.pod.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;

public class QueryClient implements Client<QueryClientProperties> {
	private static Logger logger = LoggerFactory.getLogger(QueryClient.class);

	public static void main(String[] args) throws InterruptedException, IOException, PropertyException {
		var properties = new QueryClientProperties(new PropertyManager(System.getProperties()));
		var manager = new ClientManager<>(new QueryClient(), properties);
		manager.run();
	}

	@Override
	public void run(QueryClientProperties properties) {
		// TODO: Implement client instead of logging
		logger.info("properties: {}", properties.toString());
	}
}
