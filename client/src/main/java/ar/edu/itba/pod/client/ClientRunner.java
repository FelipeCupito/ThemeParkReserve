package ar.edu.itba.pod.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ClientRunner<P extends BaseClientProperties> {
	private static Logger logger = LoggerFactory.getLogger(ClientRunner.class);

	P properties;

	public ClientRunner(P properties) {
		this.properties = properties;
	}

	public void run() throws InterruptedException {
		logger.info("Client Starting ...");
		ManagedChannel channel = ManagedChannelBuilder.forTarget(properties.getServerAddress())
				.usePlaintext()
				.build();

		try {
            Action action = properties.getAction();
            logger.info("Running action: {}", action);

			action.run();
		} finally {
			channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
		}
	}
}
