package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.List;
import java.util.UUID;

public class Utils {

    public final static int MINUTES_OF_DAY = 1440;
    public final static int DAYS_OF_YEAR = 365;

    private Utils() {

    }

    public static void checkAttractionName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Attraction name cannot be null or empty");
        }
    }

    public static void checkDay(int day) {
        if (day < 0 || day >= DAYS_OF_YEAR) {
            throw new IllegalArgumentException("Day must be between 0 and " + DAYS_OF_YEAR);
        }
    }

    public static void checkMinutes(int minutes) {
        if (minutes <= 0 || minutes >= MINUTES_OF_DAY) {
            throw new IllegalArgumentException("Minutes must be between 0 and " + MINUTES_OF_DAY);
        }
    }

    public static void checkCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
    }




}
