package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AttractionReservations {
    private final ConcurrentLinkedQueue<Reservation> reservations = new ConcurrentLinkedQueue<>();
    private Integer capacity = 0;

    public void addReservation(Reservation reservation) throws IllegalArgumentException {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (reservations.contains(reservation)) {
            throw new IllegalArgumentException("Reservation already exists");
        }
        reservations.add(reservation);
    }

    public synchronized void addReservationIfCapacityIsNotExceeded(Reservation reservation) throws IllegalArgumentException {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (reservations.contains(reservation)) {
            throw new IllegalArgumentException("Reservation already exists");
        }
        if (capacity != 0 && reservations.size() >= capacity) {
            throw new IllegalArgumentException("Capacity exceeded");
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

    public synchronized void confirmReservationIfCapacityIsNotExceeded(Reservation reservation) throws IllegalArgumentException {
        for (Reservation r : reservations) {
            if (r.equals(reservation)) {
                if (r.getStatus() == Park.ReservationType.RESERVATION_CONFIRMED) {
                    throw new IllegalArgumentException("Reservation already confirmed");
                }
                if (r.getStatus() == Park.ReservationType.RESERVATION_UNKNOWN) {
                    throw new IllegalArgumentException("Reservation status is Unknown");
                }
                if (capacity != 0 && reservations.size() >= capacity) {
                    throw new IllegalArgumentException("Capacity exceeded");
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

    public List<Reservation> getConfirmedReservations() {
        return reservations.stream().filter(r -> r.getStatus() == Park.ReservationType.RESERVATION_CONFIRMED).toList();
    }

    public int getTotalReservations() {
        return reservations.size();
    }

    public int getTotalConfirmedReservations() {
        return (int) reservations.stream().filter(r -> r.getStatus() == Park.ReservationType.RESERVATION_CONFIRMED).count();
    }

    public synchronized void setCapacity(Integer capacity) {
        if (capacity == null || capacity < 0) {
            throw new IllegalArgumentException("Capacity must be a positive number");
        }
        if (this.capacity != 0) {
            throw new IllegalArgumentException("Capacity already set");
        }
        this.capacity = capacity;
    }

    public Integer getCapacity() {
        return capacity;
    }
}
