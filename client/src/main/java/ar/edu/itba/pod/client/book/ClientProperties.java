package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.client.BaseClientProperties;
import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.book.actions.*;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;

import java.io.IOException;

public class ClientProperties extends BaseClientProperties {
    private enum ActionType {
        Attractions,
        Availability,
        Book,
        Confirm,
        Cancel
    }

    private ClientAction action;

    public ClientProperties(PropertyManager properties) throws PropertyException, IOException {
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

    public ClientAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return String.format("{action: %s}", action.toString());
    }
}
