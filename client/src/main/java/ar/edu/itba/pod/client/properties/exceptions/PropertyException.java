package ar.edu.itba.pod.client.properties.exceptions;

public abstract class PropertyException extends Exception {
	private String propertyName;

	public PropertyException(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}
}
