package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.UUID;

public class Reservation {
    private final String attractionName; // Not mutable
    private final Integer day; // Not mutable
    private final Integer openTime; // Not mutable
    private final UUID userId; // Not mutable
    private Park.ReservationType status;


    public Reservation(String attractionName, Integer day, Integer openTime, UUID userId, Park.ReservationType status) {
        if (attractionName == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        if (openTime == null || openTime < 0 || openTime > 1439) {
            throw new IllegalArgumentException("Open time must be a number between 0 and 1439");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        this.attractionName = attractionName;
        this.day = day;
        this.openTime = openTime;
        this.userId = userId;
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) obj;
        return this.attractionName.equals(other.attractionName) && this.day.equals(other.day) && this.openTime.equals(other.openTime) && this.userId.equals(other.userId);
    }

    public String getAttractionName() {
        return attractionName;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getOpenTime() {
        return openTime;
    }

    public UUID getUserId() {
        return userId;
    }

    public Park.ReservationType getStatus() {
        return status;
    }

    public synchronized void setStatus(Park.ReservationType status) {
        this.status = status;
    }
}
