package ar.edu.itba.pod.client.parsers;

import ar.edu.itba.pod.client.parsers.exceptions.ParseException;

import java.util.regex.Pattern;

public class UUIDParser implements Parser<String> {
    @Override
    public String parse(String value) throws ParseException {
        String uppercaseValue = value.toUpperCase();
        String pattern = "^\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}$";
        if (!Pattern.matches(pattern, uppercaseValue))
            throw new ParseException(String.format("\"%s\" is not a valid UUID", value));
        return uppercaseValue;
    }
}
