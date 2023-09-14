package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
            properties.runAction(Clients.fromChannel(channel));
        } catch (StatusRuntimeException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            logger.error("Exception", e);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
