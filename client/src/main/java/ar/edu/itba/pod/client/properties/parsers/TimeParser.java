package ar.edu.itba.pod.client.properties.parsers;

import ar.edu.itba.pod.client.properties.Parser;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;
import ar.edu.itba.pod.client.properties.exceptions.parser.TimeFormatException;

public class TimeParser implements Parser<Integer> {
    @Override
    public Integer parse(String time) throws ParseException {
        var parts = time.split(":");
        if (parts.length != 2 || parts[0].length() != 2 || parts[1].length() != 2)
            throw new TimeFormatException(time);
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            if (hours < 0 || hours >= 24 || minutes < 0 || minutes >= 60)
                throw new TimeFormatException(time);

            return hours * 60 + minutes;
        } catch (NumberFormatException e) {
            throw new TimeFormatException(time);
        }
    }
}
