package ar.edu.itba.pod.client.properties.exceptions;

public class FileDoesNotExistException extends PropertyException {
	public FileDoesNotExistException(String propertyName) {
		super(propertyName);
	}
}
