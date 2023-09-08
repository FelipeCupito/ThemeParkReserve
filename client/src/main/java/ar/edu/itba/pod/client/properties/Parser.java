package ar.edu.itba.pod.client.properties;

import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;

public interface Parser<T> {
    public T parse(String string) throws ParseException;
}
