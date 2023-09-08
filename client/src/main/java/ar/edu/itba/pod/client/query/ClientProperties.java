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

    private ClientAction action;

    public ClientProperties(PropertyManager properties) throws PropertyException, IOException {
        super(properties);
        var actionType = properties.getParsedProperty("action", (s) -> {
            switch (s) {
                case "capacity":
                    return ActionType.Capacity;
                case "confirmed":
                    return ActionType.Confirmed;
                default:
                    throw new ParseException();
            }
        });
        switch (actionType) {
            case Capacity:
                action = new CapacityAction(properties);
                break;
            case Confirmed:
                action = new ConfirmedAction(properties);
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
