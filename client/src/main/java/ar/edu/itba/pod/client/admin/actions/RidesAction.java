package ar.edu.itba.pod.client.admin.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.parsers.AttractionLineParser;
import ar.edu.itba.pod.client.parsers.CSVParser;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Park.AttractionInfo;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class RidesAction implements ClientAction {
    private static final Logger logger = LoggerFactory.getLogger(RidesAction.class);

    private Stream<AttractionInfo> attractions;

    public RidesAction(PropertyManager properties) throws PropertyException, IOException {
        attractions = new CSVParser<>(properties.getPathProperty("inPath"), new AttractionLineParser(), true)
                .toStream();
    }

    public Stream<AttractionInfo> getAttractions() {
        return attractions;
    }

    @Override
    public void run(Clients clients) {
        AtomicInteger addedCount = new AtomicInteger();
        AtomicInteger notAddedCount = new AtomicInteger();

        attractions.forEach((attraction) -> {
            try {
                clients.getAdminService().addAttraction(attraction);
                addedCount.getAndIncrement();
            } catch (StatusRuntimeException e) {
                logger.info(e.getMessage());
                notAddedCount.getAndIncrement();
            }
        });

        if (notAddedCount.get() > 0)
            System.out.printf("Cannot add %d attractions\n", notAddedCount.get());

        System.out.printf("%d attractions added\n", addedCount.get());
    }

    @Override
    public String toString() {
        return "RidesAction {}";
    }
}
