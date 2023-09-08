package ar.edu.itba.pod.client.properties.parsers;

import ar.edu.itba.pod.client.properties.Parser;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseIntegerException;

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
