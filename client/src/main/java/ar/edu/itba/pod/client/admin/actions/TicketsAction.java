package ar.edu.itba.pod.client.admin.actions;

import java.io.IOException;
import java.util.stream.Stream;

import ar.edu.itba.pod.client.ClientAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.parsers.CSVParser;
import ar.edu.itba.pod.client.properties.parsers.PassLineParser;
import services.Park.Pass;

public class TicketsAction implements ClientAction {
    private static final Logger logger = LoggerFactory.getLogger(TicketsAction.class);

    private Stream<Pass> passes;

    public TicketsAction(PropertyManager properties) throws PropertyException, IOException {
        passes = new CSVParser<>(properties.getPathProperty("inPath"), new PassLineParser(), true)
                .toStream();
    }

    @Override
    public void run() {
        // TODO: Implement

        logger.info("Tickets:");

        passes.forEach((pass) -> {
            logger.info("{\n{}}", pass.toString());
        });
    }

    public Stream<Pass> getPasses() {
        return passes;
    }

    @Override
    public String toString() {
        return "TicketsAction {}";
    }
}
