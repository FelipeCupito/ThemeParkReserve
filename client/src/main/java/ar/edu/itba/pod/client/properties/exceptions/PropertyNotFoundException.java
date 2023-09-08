package ar.edu.itba.pod.client.properties.exceptions;

public class PropertyNotFoundException extends PropertyException {
	public PropertyNotFoundException(String propertyName) {
		super(propertyName);
	}
}
