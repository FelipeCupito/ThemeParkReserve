package ar.edu.itba.pod.client.properties.exceptions.parser;

public class TimeFormatException extends ParseException {
    public TimeFormatException(String time) {
        super(String.format("\"%s\" is not a valid time", time));
    }
}
