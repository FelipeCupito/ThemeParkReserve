package ar.edu.itba.pod.client.properties;

import ar.edu.itba.pod.client.properties.exceptions.FileDoesNotExistException;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.PropertyNotFoundException;
import ar.edu.itba.pod.client.properties.exceptions.PropertyParseException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;
import ar.edu.itba.pod.client.properties.parsers.*;
import services.Park.UUID;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

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

	public <T> T getParsedProperty(String name, Parser<T> parser) throws PropertyException {
		try {
			return parser.parse(getProperty(name));
		} catch (ParseException parseException) {
			throw new PropertyParseException(name, parseException);
		}
	}

	public int getIntProperty(String name) throws PropertyException {
		return getParsedProperty(name, new IntegerParser());
	}

	public int getPositiveIntProperty(String name) throws PropertyException {
		return getParsedProperty(name, new PositiveIntegerParser());
	}

	public int getIntInRangeProperty(String name, Integer minBound, Integer maxBound) throws PropertyException {
		return getParsedProperty(name, new IntegerInRangeParser(minBound, maxBound));
	}

	public int getDayOfYearProperty(String name) throws PropertyException {
		return getParsedProperty(name, new DayOfYearParser());
	}

	public int getTimeProperty(String name) throws PropertyException {
		return getParsedProperty(name, new TimeParser());
	}

	public Path getPathProperty(String name) throws PropertyException {
		var path = Path.of(getProperty(name));
		if (!Files.exists(path))
			throw new FileDoesNotExistException(name);
		return path;
	}

	public UUID getUUIDProperty(String name) throws PropertyException {
		return UUID.newBuilder().setValue(getParsedProperty(name, new UUIDParser())).build();
	}
}
