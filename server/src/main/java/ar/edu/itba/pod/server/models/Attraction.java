package ar.edu.itba.pod.server.models;

public record Attraction(
        String name,
        Integer startTime,
        Integer endTime,
        Integer minutesPerSlot
) {

    public Attraction(String name, Integer startTime, Integer endTime, Integer minutesPerSlot) {
        // Check if startTime and EndTime are valid
        if (startTime < 0 || startTime > 1439) {
            throw new IllegalArgumentException("Start time must be between 0 and 1439");
        }
        if (endTime < 0 || endTime > 1439) {
            throw new IllegalArgumentException("End time must be between 0 and 1439");
        }
        if (startTime > endTime) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Check if minutesPerSlot is valid
        if (minutesPerSlot < 1) {
            throw new IllegalArgumentException("Minutes per slot must be greater than 1");
        }

        if (minutesPerSlot > (endTime - startTime)) {
            throw new IllegalArgumentException("Minutes per slot must be less than the duration of the attraction");
        }

        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minutesPerSlot = minutesPerSlot;
    }
}