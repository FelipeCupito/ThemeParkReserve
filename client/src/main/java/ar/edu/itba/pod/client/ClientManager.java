package ar.edu.itba.pod.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ClientManager<P extends BaseClientProperties> {
	private static Logger logger = LoggerFactory.getLogger(ClientManager.class);

	Client<P> client;
	P properties;

	public ClientManager(Client<P> client, P properties) {
		this.client = client;
		this.properties = properties;
	}

	public void run() throws InterruptedException {
        logger.info("Client Starting ...");
        ManagedChannel channel = ManagedChannelBuilder.forTarget(properties.getServerAddress())
                .usePlaintext()
                .build();

        try {
			client.run(properties);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
