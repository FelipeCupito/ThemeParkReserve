package ar.edu.itba.pod.server.models;

import java.util.ArrayList;
import java.util.List;

public class Attraction {
    private final static int MINUTES_OF_DAY = 1440;
    private final static int DAYS_OF_YEAR = 365;
    private final String name;
    private final int openTime;
    private final int closeTime;
    private final Integer slotDuration;
    private Integer slotsPerDay;
    private final List<AttractionDay> days = new ArrayList<>(DAYS_OF_YEAR);

    public Attraction(String name, int openTime, int closeTime, int slotDuration) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Attraction name cannot be null or empty");
        }
        if (openTime < 0 || openTime > MINUTES_OF_DAY || closeTime < 0 || closeTime > MINUTES_OF_DAY) {
            throw new IllegalArgumentException("Attraction open time must be between 0 and " + MINUTES_OF_DAY);
        }
        if (slotDuration <= 0 || slotDuration > MINUTES_OF_DAY || slotDuration > (closeTime - openTime)) {
            throw new IllegalArgumentException("Attraction slot duration must be between 0 and " + MINUTES_OF_DAY);
        }

        this.name = name;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.slotDuration = slotDuration;

        this.slotsPerDay = Attraction.calculateSlotsPerDay(openTime, closeTime, slotDuration);

        for (int i = 0; i < DAYS_OF_YEAR; i++) {
            days.add(new AttractionDay(slotsPerDay));
        }
    }

    private static int calculateSlotsPerDay(int openTime, int closeTime, int slotDuration) {
        // Calcular la cantidad de minutos entre el horario de apertura y cierre
        int duration = closeTime - openTime;

        // Calcular la cantidad de slots completo sin tener en cuenta el último
        int slotsPerDay = duration / slotDuration;

        // Compruebo si el último slot tiene la duración completa, si no lo agrego
        if (duration % slotDuration > 0) {
            slotsPerDay++;
        }
        return slotsPerDay;
    }

    public void setDayCapacity(Integer day, Integer capacity){
        if (day < 1 || day > DAYS_OF_YEAR) {
            throw new IllegalArgumentException("Attraction day must be between 0 and " + DAYS_OF_YEAR);
        }
        this.days.get(day).setCapacity(capacity);
    }
}
