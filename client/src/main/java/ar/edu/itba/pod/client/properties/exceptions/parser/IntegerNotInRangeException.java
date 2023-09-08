package ar.edu.itba.pod.client.properties.exceptions.parser;

// Includes minBound, excludes maxBound
public class IntegerNotInRangeException extends ParseException {
    private Integer minBound;
    private Integer maxBound;

    public IntegerNotInRangeException(Integer value, Integer minBound, Integer maxBound) {
        super(String.format("%d <= value (%d) < %d is false", minBound, value, maxBound));
        this.minBound = minBound;
        this.maxBound = maxBound;
    }
}
