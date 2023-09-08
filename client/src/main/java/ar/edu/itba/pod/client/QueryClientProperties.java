package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class QueryClientAction implements Action {
    int day;

    public QueryClientAction(PropertyManager properties) throws PropertyException {
        // TODO: Check argument naming
        day = properties.getDayOfYearProperty("day");
    }

    @Override
    public String toString() {
        return String.format("{ day: %d }", day);
    }
}

class CapacityAction extends QueryClientAction {
    private static Logger logger = LoggerFactory.getLogger(CapacityAction.class);

    public CapacityAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("CapacityAction %s", super.toString());
    }
}

class ConfirmedAction extends QueryClientAction {
    private static Logger logger = LoggerFactory.getLogger(ConfirmedAction.class);

    public ConfirmedAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("ConfirmedAction %s", super.toString());
    }
}

public class QueryClientProperties extends BaseClientProperties {
    private enum ActionType {
        Capacity,
        Confirmed
    }

    private QueryClientAction action;

    public QueryClientProperties(PropertyManager properties) throws PropertyException, IOException {
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

    public QueryClientAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return String.format("{action: %s}", action.toString());
    }
}
