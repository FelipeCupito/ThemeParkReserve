package ar.edu.itba.pod.client.admin.actions;

import java.io.IOException;
import java.util.stream.Stream;

import ar.edu.itba.pod.client.ClientAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.parsers.AttractionLineParser;
import ar.edu.itba.pod.client.properties.parsers.CSVParser;
import services.Park.Attraction;

public class RidesAction implements ClientAction {
    private static final Logger logger = LoggerFactory.getLogger(RidesAction.class);

    private Stream<Attraction> attractions;

    public RidesAction(PropertyManager properties) throws PropertyException, IOException {
        attractions = new CSVParser<>(properties.getPathProperty("inPath"), new AttractionLineParser(), true)
                .toStream();
    }

    public Stream<Attraction> getAttractions() {
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
