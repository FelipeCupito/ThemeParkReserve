package ar.edu.itba.pod.client.properties.exceptions;

public abstract class PropertyException extends Exception {
	private String propertyName;

	public PropertyException(String propertyName) {
		super(String.format("Error on property with name: \"%s\"", propertyName));
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}
}
