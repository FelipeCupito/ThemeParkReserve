package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.PropertyNotFoundException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;
import services.Park.UUID;

import java.io.IOException;

abstract class BookClientAction {

}

class AttractionsAction extends BookClientAction {
}

class AvailabilityAction extends BookClientAction {
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
	public String toString() {
		return String.format("{day: %d, attraction: \"%s\", slot: %d, slotTo: %d}", day, attraction, slot, slotTo);
	}
}

class BookAction extends BookClientAction {
	UUID visitor;
	int day;
	String attraction;
	int slot;

	public BookAction(PropertyManager properties) throws PropertyException {
		// TODO: Check argument naming
		visitor = properties.getUUIDProperty("visitor");
		day = properties.getDayOfYearProperty("day");
		attraction = properties.getProperty("attraction");
		slot = properties.getTimeProperty("slot");
	}

	@Override
	public String toString() {
		return String.format("{visitor: \"%s\", day: %d, attraction: \"%s\", slot: %d}", visitor, day, attraction, slot);
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
