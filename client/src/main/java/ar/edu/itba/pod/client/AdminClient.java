package ar.edu.itba.pod.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import services.Park.Attraction;
import services.Park.Pass;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.stream.Stream;

abstract class Action {

}

class RidesAction extends Action {
	private Stream<Attraction> attractions;

	public RidesAction(PropertyManager properties) throws PropertyException, IOException {
		attractions = Files
				.lines(properties.getPathProperty("inPath"))
				.skip(1)
				.map(RidesAction::parseLine);
	}

	private static Attraction parseLine(String line) {
		// TODO: Implement line parser
		var parts = line.split(",");

		throw new UnsupportedOperationException("Not yet implemented");

		//return Attraction.newBuilder()
				//.build();
	}

	public Stream<Attraction> getAttractions() {
		return attractions;
	}
}

class TicketsAction extends Action {
	private Stream<Pass> passes;

	public TicketsAction(PropertyManager properties) throws PropertyException, IOException {
		passes = Files
				.lines(properties.getPathProperty("inPath"))
				.skip(1)
				.map(TicketsAction::parseLine);
	}

	private static Pass parseLine(String line) {
		// TODO: Implement line parser
		var parts = line.split(",");

		throw new UnsupportedOperationException("Not yet implemented");

		//return Pass.newBuilder()
				//.build();
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
		rideName = properties.getProperty("rideName");
		dayOfYear = properties.getDayOfYearProperty("rideName");
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

class AdminProperties extends BaseClientProperties {
	private Action action;

	public AdminProperties(PropertyManager properties) throws PropertyException, IOException {
		super(properties);
		switch (properties.getProperty("action")) {
			case "rides":
				action = new RidesAction(properties);
				break;
			case "tickets":
				action = new TicketsAction(properties);
				break;
			case "slots":
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

public class AdminClient implements Client<AdminProperties> {
	private static Logger logger = LoggerFactory.getLogger(AdminClient.class);

	public static void main(String[] args) throws InterruptedException, IOException, PropertyException {
		var properties = new AdminProperties(new PropertyManager(System.getProperties()));
		var manager = new ClientManager<>(new AdminClient(), properties);
		manager.run();
	}

	@Override
	public void run(AdminProperties properties) {
		System.out.printf("properties: %s", properties.toString());
	}
}
