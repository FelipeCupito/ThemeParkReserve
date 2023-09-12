package ar.edu.itba.pod.client.properties.exceptions;

import ar.edu.itba.pod.client.parsers.exceptions.ParseException;

public class PropertyParseException extends PropertyException {
    public PropertyParseException(String propertyName, ParseException parseException) {
        super(propertyName, parseException.toString());
    }
}
