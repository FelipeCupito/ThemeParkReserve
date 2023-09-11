package ar.edu.itba.pod.client.parsers;

import ar.edu.itba.pod.client.parsers.exceptions.ParseException;
import services.Park.AttractionInfo;

public class AttractionLineParser extends CSVLineParser<AttractionInfo> {
    public AttractionLineParser() {
        super(4);
    }

    @Override
    AttractionInfo parseFields(String[] parts) throws ParseException {
        String name = parts[0];
        int openTime = new TimeParser().parse(parts[1]);
        int closeTime = new TimeParser().parse(parts[2]);
        int minutesPerSlot = new PositiveIntegerParser().parse(parts[3]);
        return AttractionInfo
                .newBuilder()
                .setName(name)
                .setOpenTime(openTime)
                .setCloseTime(closeTime)
                .setSlotDuration(minutesPerSlot)
                .build();
    }
}
