package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.client.BaseClientProperties;
import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.book.actions.*;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.parsers.exceptions.ParseException;

import java.io.IOException;

public class ClientProperties extends BaseClientProperties {
    private enum ActionType {
        Attractions,
        Availability,
        Book,
        Confirm,
        Cancel
    }

    private final ClientAction action;

    public ClientProperties(PropertyManager properties) throws PropertyException, IOException {
        super(properties);
        var actionType = properties.getParsedProperty("action", (s) -> switch (s) {
            case "attractions" -> ActionType.Attractions;
            case "availability" -> ActionType.Availability;
            case "book" -> ActionType.Book;
            case "confirm" -> ActionType.Confirm;
            case "cancel" -> ActionType.Cancel;
            default -> throw new ParseException();
        });
        action = switch (actionType) {
            case Attractions -> new AttractionsAction();
            case Availability -> new AvailabilityAction(properties);
            case Book -> new BookAction(properties);
            case Confirm -> new ConfirmAction(properties);
            case Cancel -> new CancelAction(properties);
        };
    }

    public ClientAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return String.format("{action: %s}", action.toString());
    }
}
