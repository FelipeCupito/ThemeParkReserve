package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AttractionReservations {
    private final ConcurrentLinkedQueue<Reservation> reservations = new ConcurrentLinkedQueue<>();

    public void addReservation(Reservation reservation) throws IllegalArgumentException {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (reservations.contains(reservation)) {
            throw new IllegalArgumentException("Reservation already exists");
        }
        reservations.add(reservation);
    }

    public void confirmReservation(Reservation reservation) throws IllegalArgumentException {
        for (Reservation r : reservations) {
            if (r.equals(reservation)) {
                    if (r.getStatus() == Park.ReservationType.RESERVATION_CONFIRMED) {
                        throw new IllegalArgumentException("Reservation already confirmed");
                    }
                    if (r.getStatus() == Park.ReservationType.RESERVATION_UNKNOWN) {
                        throw new IllegalArgumentException("Reservation status is Unknown");
                    }
                    r.setStatus(Park.ReservationType.RESERVATION_CONFIRMED);
                return;
            }
        }
        throw new IllegalArgumentException("Reservation does not exist");
    }

    public void cancelReservation(Reservation reservation) throws IllegalArgumentException {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (!reservations.contains(reservation)) {
            throw new IllegalArgumentException("Reservation does not exist");
        }
        reservations.remove(reservation);
    }

    public List<Reservation> getReservations() {
        return reservations.stream().toList();
    }
}
