package ar.edu.itba.pod.client.admin.actions;

import java.io.IOException;
import java.util.stream.Stream;

import ar.edu.itba.pod.client.ClientAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.parsers.AttractionLineParser;
import ar.edu.itba.pod.client.parsers.CSVParser;
import services.Park.AttractionInfo;

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
    public void run() {
        // TODO: Implement

        logger.info("Attractions:");

        attractions.forEach((attraction) -> {
            logger.info("{\n{}}", attraction.toString());
        });
    }

    @Override
    public String toString() {
        return "RidesAction {}";
    }
}
