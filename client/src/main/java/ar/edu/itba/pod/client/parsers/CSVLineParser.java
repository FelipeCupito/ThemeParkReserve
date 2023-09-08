package ar.edu.itba.pod.client.parsers;

import ar.edu.itba.pod.client.parsers.exceptions.CSVParseException;
import ar.edu.itba.pod.client.parsers.exceptions.ParseException;

public abstract class CSVLineParser<T> implements Parser<T> {
    private int fieldCount;

    public CSVLineParser(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    @Override
    public T parse(String string) throws ParseException {
        var fields = string.split(";");
        if (fields.length != fieldCount) {
            throw new CSVParseException();
        }
        return parseFields(fields);
    }

    abstract T parseFields(String[] parts) throws ParseException;
}
