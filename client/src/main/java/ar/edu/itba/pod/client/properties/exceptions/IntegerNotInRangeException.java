package ar.edu.itba.pod.client.properties.exceptions;

// Includes minBound, excludes maxBound
public class IntegerNotInRangeException extends PropertyException {
	private Integer minBound;
	private Integer maxBound;

	public IntegerNotInRangeException(String propertyName, Integer value, Integer minBound, Integer maxBound) {
		super(propertyName, String.format("%d <= value (%d) < %d is false", minBound, value, maxBound));
		this.minBound = minBound;
		this.maxBound = maxBound;
	}
}
