package ar.edu.itba.pod.client.parsers;

import org.junit.Test;

import ar.edu.itba.pod.client.parsers.exceptions.ParseException;
import ar.edu.itba.pod.client.parsers.exceptions.TimeFormatException;

import static org.junit.Assert.*;

public class TimeParserTest {
    @Test
    public void correctTimes() {
        try {
            var parser = new TimeParser();
            assertEquals(0, (int) parser.parse("00:00"));
            assertEquals(30, (int) parser.parse("00:30"));
            assertEquals(59, (int) parser.parse("00:59"));
            assertEquals(60, (int) parser.parse("01:00"));
        } catch (ParseException e) {
        }
    }

    @Test
    public void wrongTimes() {
        var parser = new TimeParser();
        assertThrows(TimeFormatException.class, () -> parser.parse("1:00"));
        assertThrows(TimeFormatException.class, () -> parser.parse("01:0"));
        assertThrows(TimeFormatException.class, () -> parser.parse("010:00"));
        assertThrows(TimeFormatException.class, () -> parser.parse("10,00"));
        assertThrows(TimeFormatException.class, () -> parser.parse("1a:00"));
    }
}
