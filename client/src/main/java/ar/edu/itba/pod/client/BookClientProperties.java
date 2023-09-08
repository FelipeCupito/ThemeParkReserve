package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.PropertyNotFoundException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;
import services.Park.UUID;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class BookClientAction implements Action {

}

class AttractionsAction extends BookClientAction {
    private static Logger logger = LoggerFactory.getLogger(AttractionsAction.class);

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return "AttractionsAction {}";
    }
}

class AvailabilityAction extends BookClientAction {
    private static Logger logger = LoggerFactory.getLogger(AvailabilityAction.class);

    int day;
    String attraction;
    int slot;
    Integer slotTo;

    public AvailabilityAction(PropertyManager properties) throws PropertyException {
        // TODO: Check argument naming
        day = properties.getDayOfYearProperty("day");
        slot = properties.getTimeProperty("slot");
        try {
            attraction = properties.getProperty("attraction");
        } catch (PropertyNotFoundException e) {
            // Optional property
            attraction = null;
        }
        try {
            slotTo = properties.getTimeProperty("slotTo");
        } catch (PropertyNotFoundException e) {
            // Optional property
            slotTo = null;
        }
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("AvailabilityAction { day: %d, attraction: \"%s\", slot: %d, slotTo: %d }", day,
                attraction, slot, slotTo);
    }
}

abstract class ReservationAction extends BookClientAction {
    UUID visitor;
    int day;
    String attraction;
    int slot;

    public ReservationAction(PropertyManager properties) throws PropertyException {
        // TODO: Check argument naming
        visitor = properties.getUUIDProperty("visitor");
        day = properties.getDayOfYearProperty("day");
        attraction = properties.getProperty("attraction");
        slot = properties.getTimeProperty("slot");
    }

    @Override
    public String toString() {
        return String.format("{ visitor: \"%s\", day: %d, attraction: \"%s\", slot: %d }", visitor, day, attraction,
                slot);
    }
}

class BookAction extends ReservationAction {
    private static Logger logger = LoggerFactory.getLogger(BookAction.class);

    public BookAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("BookAction %s", super.toString());
    }
}

class ConfirmAction extends ReservationAction {
    private static Logger logger = LoggerFactory.getLogger(ConfirmAction.class);

    public ConfirmAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("ConfirmAction %s", super.toString());
    }
}

class CancelAction extends ReservationAction {
    private static Logger logger = LoggerFactory.getLogger(CancelAction.class);

    public CancelAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("CancelAction %s", super.toString());
    }
}

public class BookClientProperties extends BaseClientProperties {
    private enum ActionType {
        Attractions,
        Availability,
        Book,
        Confirm,
        Cancel
    }

    private BookClientAction action;

    public BookClientProperties(PropertyManager properties) throws PropertyException, IOException {
        super(properties);
        var actionType = properties.getParsedProperty("action", (s) -> {
            switch (s) {
                case "attractions":
                    return ActionType.Attractions;
                case "availability":
                    return ActionType.Availability;
                case "book":
                    return ActionType.Book;
                case "confirm":
                    return ActionType.Confirm;
                case "cancel":
                    return ActionType.Cancel;
                default:
                    throw new ParseException();
            }
        });
        switch (actionType) {
            case Attractions:
                action = new AttractionsAction();
                break;
            case Availability:
                action = new AvailabilityAction(properties);
                break;
            case Book:
                action = new BookAction(properties);
                break;
            case Confirm:
                action = new ConfirmAction(properties);
                break;
            case Cancel:
                action = new CancelAction(properties);
                break;
        }
    }

    public BookClientAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return String.format("{action: %s}", action.toString());
    }
}
