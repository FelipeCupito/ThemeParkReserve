package ar.edu.itba.pod.client.parsers;

public class PositiveIntegerParser extends IntegerInRangeParser {
    public PositiveIntegerParser() {
        super(1, null);
    }
}
