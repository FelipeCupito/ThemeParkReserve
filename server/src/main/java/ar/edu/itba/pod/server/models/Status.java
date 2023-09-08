package ar.edu.itba.pod.server.models;
//TODO: esto tiene que estar en la api

import services.Park;

public enum Status {
    PENDING,
    CONFIRMED,
    CANCELLED;

    public static Park.ReservationType getReservationType(Status status){
        return switch (status) {
            case PENDING -> Park.ReservationType.RESERVATION_PENDING;
            case CONFIRMED -> Park.ReservationType.RESERVATION_CONFIRMED;
            case CANCELLED -> throw new IllegalArgumentException("Invalid status");
        };
    }

}
