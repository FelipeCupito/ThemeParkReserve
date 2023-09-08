package ar.edu.itba.pod.client.parsers;

import ar.edu.itba.pod.client.parsers.exceptions.ParseException;
import services.Park.Attraction;

public class AttractionLineParser extends CSVLineParser<Attraction> {
    public AttractionLineParser() {
        super(4);
    }

    @Override
    Attraction parseFields(String[] parts) throws ParseException {
        String name = parts[0];
        int openTime = new TimeParser().parse(parts[1]);
        int closeTime = new TimeParser().parse(parts[2]);
        int minutesPerSlot = new PositiveIntegerParser().parse(parts[3]);
        return Attraction
                .newBuilder()
                .setName(name)
                .setOpenTime(openTime)
                .setCloseTime(closeTime)
                .setMinutesPerSlot(minutesPerSlot)
                .build();
    }
}
