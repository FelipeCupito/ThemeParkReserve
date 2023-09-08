package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.PropertyNotFoundException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;
import services.Park.UUID;

import java.io.IOException;

abstract class NotificationClientAction {
	UUID visitor;
	int day;
	String attraction;

	public NotificationClientAction(PropertyManager properties) throws PropertyException {
		// TODO: Check argument naming
		visitor = properties.getUUIDProperty("visitor");
		day = properties.getDayOfYearProperty("day");
		attraction = properties.getProperty("attraction");
	}

	@Override
	public String toString() {
		return String.format("{visitor: \"%s\", day: %d, attraction: \"%s\"}", visitor, day, attraction);
	}
}

class FollowAction extends NotificationClientAction {
	public FollowAction(PropertyManager properties) throws PropertyException {
		super(properties);
	}
}

class UnfollowAction extends NotificationClientAction {
	public UnfollowAction(PropertyManager properties) throws PropertyException {
		super(properties);
	}
}

public class NotificationClientProperties extends BaseClientProperties {
	private enum ActionType {
		Follow,
		Unfollow
	}

	private NotificationClientAction action;

	public NotificationClientProperties(PropertyManager properties) throws PropertyException, IOException {
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

	public NotificationClientAction getAction() {
		return action;
	}

	@Override
	public String toString() {
		return String.format("{action: %s}", action.toString());
	}
}
