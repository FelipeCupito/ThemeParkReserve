package ar.edu.itba.pod.client.properties.parsers;

public class PositiveIntegerParser extends IntegerInRangeParser {
    public PositiveIntegerParser() {
        super(1, null);
    }
}
