package ar.edu.itba.pod.client.properties.exceptions;

// Includes minBound, excludes maxBound
public class IntegerNotInRangeException extends PropertyException {
	private Integer minBound;
	private Integer maxBound;

	public IntegerNotInRangeException(String propertyName, Integer minBound, Integer maxBound) {
		super(propertyName);
		this.minBound = minBound;
		this.maxBound = maxBound;
	}
}
