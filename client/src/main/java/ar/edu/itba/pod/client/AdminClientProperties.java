package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;
import ar.edu.itba.pod.client.properties.parsers.AttractionLineParser;
import ar.edu.itba.pod.client.properties.parsers.CSVParser;
import ar.edu.itba.pod.client.properties.parsers.PassLineParser;
import services.Park.Attraction;
import services.Park.Pass;

import java.io.IOException;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AdminAction implements Action {

}

class RidesAction extends AdminAction {
    private static Logger logger = LoggerFactory.getLogger(RidesAction.class);

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

class TicketsAction extends AdminAction {
    private static Logger logger = LoggerFactory.getLogger(TicketsAction.class);

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

class SlotsAction extends AdminAction {
    private static Logger logger = LoggerFactory.getLogger(SlotsAction.class);

    String rideName;
    int dayOfYear;
    int amount;

    public SlotsAction(String rideName, int dayOfYear, int amount) {
        this.rideName = rideName;
        this.dayOfYear = dayOfYear;
        this.amount = amount;
    }

    public SlotsAction(PropertyManager properties) throws PropertyException {
        // TODO: check if parameter names are correct acording to spec (spec is
        // inconsistent)
        rideName = properties.getProperty("rideName");
        dayOfYear = properties.getDayOfYearProperty("dayOfYear");
        amount = properties.getPositiveIntProperty("amount");
    }

    @Override
    public void run() {
        // TODO: Implement

        logger.info(toString());
    }

    public String getRideName() {
        return rideName;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("SlotsAction { rideName: \"%s\", dayOfYear: %d, amount: %d }", rideName, dayOfYear,
                amount);
    }
}

public class AdminClientProperties extends BaseClientProperties {
    private enum ActionType {
        Rides,
        Tickets,
        Slots
    }

    private AdminAction action;

    public AdminClientProperties(PropertyManager properties) throws PropertyException, IOException {
        super(properties);
        var actionType = properties.getParsedProperty("action", (s) -> {
            switch (s) {
                case "rides":
                    return ActionType.Rides;
                case "tickets":
                    return ActionType.Tickets;
                case "slots":
                    return ActionType.Slots;
                default:
                    throw new ParseException();
            }
        });
        switch (actionType) {
            case Rides:
                action = new RidesAction(properties);
                break;
            case Tickets:
                action = new TicketsAction(properties);
                break;
            case Slots:
                action = new SlotsAction(properties);
                break;
        }
    }

    public AdminAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return String.format("{action: %s}", action.toString());
    }
}
