package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.client.BaseClientProperties;
import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.admin.actions.RidesAction;
import ar.edu.itba.pod.client.admin.actions.SlotsAction;
import ar.edu.itba.pod.client.admin.actions.TicketsAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;

import java.io.IOException;

public class ClientProperties extends BaseClientProperties {
    private enum ActionType {
        Rides,
        Tickets,
        Slots
    }

    private final ClientAction action;

    public ClientProperties(PropertyManager properties) throws PropertyException, IOException {
        super(properties);
        var actionType = properties.getParsedProperty("action", (s) -> switch (s) {
            case "rides" -> ActionType.Rides;
            case "tickets" -> ActionType.Tickets;
            case "slots" -> ActionType.Slots;
            default -> throw new ParseException();
        });
        action = switch (actionType) {
            case Rides -> new RidesAction(properties);
            case Tickets -> new TicketsAction(properties);
            case Slots -> new SlotsAction(properties);
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
