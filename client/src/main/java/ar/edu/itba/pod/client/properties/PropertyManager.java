package ar.edu.itba.pod.client.properties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Function;

import ar.edu.itba.pod.client.properties.exceptions.*;

public class PropertyManager {
	Properties properties;

	public PropertyManager(Properties properties) {
		this.properties = properties;
	}

	public String getProperty(String name) throws PropertyNotFoundException {
		var value = properties.getProperty(name);
		if (value == null)
			throw new PropertyNotFoundException(name);

		return value;
	}

	/**
	 * @param parser should return null on parsing error
	 */
	public <T> T getParsedProperty(String name, Function<String, T> parser) throws PropertyException {
		var value = parser.apply(getProperty(name));
		if (value == null)
			throw new PropertyParseException(name);

		return value;
	}

	public int getIntProperty(String name) throws PropertyException {
		try {
			return Integer.parseInt(getProperty(name));
		} catch (NumberFormatException e) {
			throw new ParseIntegerException(name);
		} catch (PropertyException e) {
			throw e;
		}
	}

	public int getPositiveIntProperty(String name) throws PropertyException {
		int value = getIntProperty(name);
		if (value <= 0)
			throw new IntegerNotPositiveException(name);
		return value;
	}

	public int getIntInRangeProperty(String name, Integer minBound, Integer maxBound) throws PropertyException {
		int value = getIntProperty(name);
		if ((minBound != null && value < minBound) || (maxBound != null && value >= maxBound))
			throw new IntegerNotInRangeException(name, value, minBound, maxBound);

		return value;
	}

	/**
	 * @return day of the year starting at 0
	 */
	public int getDayOfYearProperty(String name) throws PropertyException {
		return getIntInRangeProperty(name, 1, 365 + 1) - 1;
	}

	public int getTimeProperty(String name) throws PropertyException {
		var time = getProperty(name);
		var parts = time.split(":");
		if (parts.length != 2 || parts[0].length() != 2 || parts[1].length() != 2)
			throw new TimeFormatException(name);
		try {
			int hours = Integer.parseInt(parts[0]);
			int minutes = Integer.parseInt(parts[1]);

			if (hours < 0 || hours >= 24 || minutes < 0 || minutes >= 60)
				throw new TimeFormatException(name);

			return hours * 60 + minutes;
		} catch (NumberFormatException e) {
			throw new TimeFormatException(name);
		}
	}

	public Path getPathProperty(String name) throws PropertyException {
		var path = Path.of(getProperty(name));
		if (!Files.exists(path))
			throw new FileDoesNotExistException(name);
		return path;
	}
}
