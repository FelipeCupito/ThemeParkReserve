package ar.edu.itba.pod.client.admin.actions;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.Clients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.parsers.CSVParser;
import ar.edu.itba.pod.client.parsers.PassLineParser;
import services.Park.PassRequest;

public class TicketsAction implements ClientAction {
    private static final Logger logger = LoggerFactory.getLogger(TicketsAction.class);

    private Stream<PassRequest> passes;

    public TicketsAction(PropertyManager properties) throws PropertyException, IOException {
        passes = new CSVParser<>(properties.getPathProperty("inPath"), new PassLineParser(), true)
                .toStream();
    }

    @Override
    public void run(Clients clients) {
        AtomicInteger addedCount = new AtomicInteger();
        AtomicInteger notAddedCount = new AtomicInteger();

        passes.forEach((pass) -> {
            // TODO: Check if pass was added succesfully
            try {
                clients.getAdminService().addPass(pass);
                addedCount.getAndIncrement();
            } catch (Exception e) {
                notAddedCount.getAndIncrement();
            }
        });

        if (notAddedCount.get() > 0)
            logger.info("Cannot add {} passes", notAddedCount.get());

        logger.info("{} passes added", addedCount.get());
    }

    public Stream<PassRequest> getPasses() {
        return passes;
    }

    @Override
    public String toString() {
        return "TicketsAction {}";
    }
}
