package ar.edu.itba.pod.client.notification;

import ar.edu.itba.pod.client.BaseClientProperties;
import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.notification.actions.FollowAction;
import ar.edu.itba.pod.client.notification.actions.UnfollowAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;

import java.io.IOException;

public class ClientProperties extends BaseClientProperties {
    private enum ActionType {
        Follow,
        Unfollow
    }

    private ClientAction action;

    public ClientProperties(PropertyManager properties) throws PropertyException, IOException {
        super(properties);
        var actionType = properties.getParsedProperty("action", (s) -> {
            switch (s) {
                case "follow":
                    return ActionType.Follow;
                case "unfollow":
                    return ActionType.Unfollow;
                default:
                    throw new ParseException();
            }
        });
        switch (actionType) {
            case Follow:
                action = new FollowAction(properties);
                break;
            case Unfollow:
                action = new UnfollowAction(properties);
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
