package ar.edu.itba.pod.client.parsers;

import ar.edu.itba.pod.client.parsers.exceptions.IntegerNotInRangeException;
import ar.edu.itba.pod.client.parsers.exceptions.ParseException;

public class IntegerInRangeParser implements Parser<Integer> {
    Integer minBound;
    Integer maxBound;

    public IntegerInRangeParser(Integer minBound, Integer maxBound) {
        this.minBound = minBound;
        this.maxBound = maxBound;
    }

    @Override
    public Integer parse(String string) throws ParseException {
        var value = new IntegerParser().parse(string);

        if ((minBound != null && value < minBound) || (maxBound != null && value >= maxBound))
            throw new IntegerNotInRangeException(value, minBound, maxBound);

        return value;
    }
}
