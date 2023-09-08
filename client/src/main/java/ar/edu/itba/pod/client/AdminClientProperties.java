package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;
import ar.edu.itba.pod.client.properties.parsers.AttractionLineParser;
import ar.edu.itba.pod.client.properties.parsers.CSVParser;
import ar.edu.itba.pod.client.properties.parsers.PassLineParser;
import services.Park.Attraction;
import services.Park.Pass;

import java.io.IOException;
import java.util.stream.Stream;

abstract class Action {

}

class RidesAction extends Action {
	private Stream<Attraction> attractions;

	public RidesAction(PropertyManager properties) throws PropertyException, IOException {
		attractions = new CSVParser<>(properties.getPathProperty("inPath"), new AttractionLineParser(), true)
				.toStream();
	}

	public Stream<Attraction> getAttractions() {
		return attractions;
	}
}

class TicketsAction extends Action {
	private Stream<Pass> passes;

	public TicketsAction(PropertyManager properties) throws PropertyException, IOException {
		passes = new CSVParser<>(properties.getPathProperty("inPath"), new PassLineParser(), true)
				.toStream();
	}

	public Stream<Pass> getPasses() {
		return passes;
	}
}

class SlotsAction extends Action {
	String rideName;
	int dayOfYear;
	int amount;

	public SlotsAction(String rideName, int dayOfYear, int amount) {
		this.rideName = rideName;
		this.dayOfYear = dayOfYear;
		this.amount = amount;
	}

	public SlotsAction(PropertyManager properties) throws PropertyException {
		// TODO: check if parameter names are correct acording to spec (spec is
		// inconsistent)
		rideName = properties.getProperty("rideName");
		dayOfYear = properties.getDayOfYearProperty("dayOfYear");
		amount = properties.getPositiveIntProperty("amount");
	}

	public String getRideName() {
		return rideName;
	}

	public int getDayOfYear() {
		return dayOfYear;
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return String.format("{rideName: \"%s\", dayOfYear: %d, amount: %d}", rideName, dayOfYear, amount);
	}
}

public class AdminClientProperties extends BaseClientProperties {
	private enum ActionType {
		Rides,
		Tickets,
		Slots
	}

	private Action action;

	public AdminClientProperties(PropertyManager properties) throws PropertyException, IOException {
		super(properties);
		var actionType = properties.getParsedProperty("action", (s) -> {
			switch (s) {
				case "rides":
					return ActionType.Rides;
				case "tickets":
					return ActionType.Tickets;
				case "slots":
					return ActionType.Slots;
				default:
					throw new ParseException();
			}
		});
		switch (actionType) {
			case Rides:
				action = new RidesAction(properties);
				break;
			case Tickets:
				action = new TicketsAction(properties);
				break;
			case Slots:
				action = new SlotsAction(properties);
				break;
		}
	}

	public Action getAction() {
		return action;
	}

	@Override
	public String toString() {
		return String.format("{action: %s}", action.toString());
	}
}
