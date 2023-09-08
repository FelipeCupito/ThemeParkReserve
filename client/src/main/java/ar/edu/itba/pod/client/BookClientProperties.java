package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.PropertyNotFoundException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;

import java.io.IOException;

abstract class BookAction {

}

class AttractionsAction extends BookAction {
}

class AvailabilityAction extends BookAction {
	int day;
	String attraction;
	int slot;
	Integer slotTo;

	public AvailabilityAction(PropertyManager properties) throws PropertyException {
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

public class BookClientProperties extends BaseClientProperties {
	private enum ActionType {
		Attractions,
		Availability,
		Book,
		Confirm,
		Cancel
	}

	private BookAction action;

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
		}
	}

	public BookAction getAction() {
		return action;
	}

	@Override
	public String toString() {
		return String.format("{action: %s}", action.toString());
	}
}
