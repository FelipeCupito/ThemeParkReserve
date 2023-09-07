package ar.edu.itba.pod.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

//class SlotsAction

class AdminProperties extends BaseClientProperties {
	//private Action action;
	public AdminProperties(Properties properties) {
		super(properties);
	}
}

public class AdminClient implements Client<AdminProperties> {
    private static Logger logger = LoggerFactory.getLogger(AdminClient.class);

	public static void main(String[] args) throws InterruptedException {
		var properties = new AdminProperties(System.getProperties());
		var manager = new ClientManager<>(new AdminClient(), properties);
		manager.run();
	}

	@Override
	public void run(AdminProperties properties) {
		System.out.printf("Address: %s", properties.getServerAddress());
	}
}
