package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.BaseClientProperties;
import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;
import ar.edu.itba.pod.client.query.actions.CapacityAction;
import ar.edu.itba.pod.client.query.actions.ConfirmedAction;

import java.io.IOException;

public class ClientProperties extends BaseClientProperties {
    private enum ActionType {
        Capacity,
        Confirmed
    }

    private final ClientAction action;

    public ClientProperties(PropertyManager properties) throws PropertyException, IOException {
        super(properties);
        var actionType = properties.getParsedProperty("action", (s) -> switch (s) {
            case "capacity" -> ActionType.Capacity;
            case "confirmed" -> ActionType.Confirmed;
            default -> throw new ParseException();
        });
        action = switch (actionType) {
            case Capacity -> new CapacityAction(properties);
            case Confirmed -> new ConfirmedAction(properties);
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
