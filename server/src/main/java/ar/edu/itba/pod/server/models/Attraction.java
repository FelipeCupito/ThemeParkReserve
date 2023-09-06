package ar.edu.itba.pod.server.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Attraction {
    private final static int DAYS_OF_YEAR = 365;
    private final String name;
    private final LocalDate openingDate;
    private final LocalDate closingDate;
    private final Integer slotDuration;
    private Integer slotsPerDay;
    private final List<AttractionDay> days = new ArrayList<>(DAYS_OF_YEAR);

    public Attraction(String name, LocalDate openingDate, LocalDate closingDate, int slotDuration) {
        this.name = name;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.slotDuration = slotDuration;

        //TODO: calculate number of solts per day
        this.slotsPerDay = 0;

        for (int i = 0; i < DAYS_OF_YEAR; i++) {
            days.set(i, new AttractionDay(slotsPerDay));
        }
    }
}
