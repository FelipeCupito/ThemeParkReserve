package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.concurrent.atomic.LongAccumulator;

public class Reservation {
    private static Long CONFIRMATION_ORDER = 0L;

    private final String attractionName; // Not mutable
    private final Integer day; // Not mutable
    private Integer openTime; // Not mutable
    private final Park.UUID userId; // Not mutable
    private Park.ReservationType status;
    private final Integer duration;
    private Long confirmationOrder = 0L;


    public Reservation(String attractionName, Integer day, Integer openTime, Park.UUID userId, Park.ReservationType status, Integer duration) {
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
        if (duration == null || duration < 1) {
            throw new IllegalArgumentException("Duration must be a number greater than 0");
        }

        this.attractionName = attractionName;
        this.day = day;
        this.openTime = openTime;
        this.userId = userId;
        this.status = status;
        this.duration = duration;
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

    public Park.UUID getUserId() {
        return userId;
    }

    public Park.ReservationType getStatus() {
        return status;
    }

    public synchronized void setStatus(Park.ReservationType status) {
        this.status = status;
        if (status == Park.ReservationType.RESERVATION_CONFIRMED) {
            this.confirmationOrder = CONFIRMATION_ORDER;
            CONFIRMATION_ORDER += 1L;
        }
    }

    public synchronized void setOpenTime(Integer openTime) {
        this.openTime = openTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public Long getConfirmationOrder() {
        return confirmationOrder;
    }
}
