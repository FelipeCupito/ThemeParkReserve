package ar.edu.itba.pod.client.notification;

import ar.edu.itba.pod.client.BaseClientProperties;
import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.notification.actions.FollowAction;
import ar.edu.itba.pod.client.notification.actions.UnfollowAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.parsers.exceptions.ParseException;

import java.io.IOException;

public class ClientProperties extends BaseClientProperties {
    private enum ActionType {
        Follow,
        Unfollow
    }

    private final ClientAction action;

    public ClientProperties(PropertyManager properties) throws PropertyException, IOException {
        super(properties);
        var actionType = properties.getParsedProperty("action", (s) -> switch (s) {
            case "follow" -> ActionType.Follow;
            case "unfollow" -> ActionType.Unfollow;
            default -> throw new ParseException();
        });
        action = switch (actionType) {
            case Follow -> new FollowAction(properties);
            case Unfollow -> new UnfollowAction(properties);
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
