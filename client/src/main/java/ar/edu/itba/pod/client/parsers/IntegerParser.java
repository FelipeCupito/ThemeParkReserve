package ar.edu.itba.pod.client.parsers;

import ar.edu.itba.pod.client.parsers.exceptions.ParseException;
import ar.edu.itba.pod.client.parsers.exceptions.ParseIntegerException;

public class IntegerParser implements Parser<Integer> {
    @Override
    public Integer parse(String string) throws ParseException {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new ParseIntegerException();
        }
    }
}
