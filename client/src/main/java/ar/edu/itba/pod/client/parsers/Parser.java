package ar.edu.itba.pod.client.parsers;

import ar.edu.itba.pod.client.parsers.exceptions.ParseException;

public interface Parser<T> {
    T parse(String string) throws ParseException;
}
