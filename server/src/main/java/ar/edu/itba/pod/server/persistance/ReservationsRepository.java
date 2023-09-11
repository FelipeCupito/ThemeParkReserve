package ar.edu.itba.pod.server.persistance;

import ar.edu.itba.pod.server.models.AttractionReservations;
import ar.edu.itba.pod.server.models.Reservation;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReservationsRepository {
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, AttractionReservations>> reservationsPerDay = new ConcurrentHashMap<>();

    public void addReservation(Reservation reservation) throws IllegalArgumentException {
        // It assumes that the attraction already Exists
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        String name = reservation.getAttractionName();
        if (name == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }

        Integer day = reservation.getDay();
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        reservationsPerDay.putIfAbsent(day, new ConcurrentHashMap<>());
        reservationsPerDay.get(day).putIfAbsent(name, new AttractionReservations());
        reservationsPerDay.get(day).get(name).addReservation(reservation);
    }

    public void cancelReservation(Reservation reservation) throws IllegalArgumentException {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        String name = reservation.getAttractionName();
        if (name == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }

        Integer day = reservation.getDay();
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        reservationsPerDay.putIfAbsent(day, new ConcurrentHashMap<>());
        reservationsPerDay.get(day).putIfAbsent(name, new AttractionReservations());
        reservationsPerDay.get(day).get(name).cancelReservation(reservation);
    }

    public void confirmReservation(Reservation reservation) throws IllegalArgumentException {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        String name = reservation.getAttractionName();
        if (name == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }

        Integer day = reservation.getDay();
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        reservationsPerDay.putIfAbsent(day, new ConcurrentHashMap<>());
        reservationsPerDay.get(day).putIfAbsent(name, new AttractionReservations());
        reservationsPerDay.get(day).get(name).confirmReservation(reservation);
    }

    public synchronized List<Reservation> getReservations(Integer day, String attractionName) throws IllegalArgumentException {
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        if (attractionName == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }
        reservationsPerDay.putIfAbsent(day, new ConcurrentHashMap<>());
        reservationsPerDay.get(day).putIfAbsent(attractionName, new AttractionReservations());
        return reservationsPerDay.get(day).get(attractionName).getReservations();
    }

    public void setCapacity(Integer day, String attractionName, Integer capacity) {
        // Assumes that the attractionName is valid
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        if (attractionName == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }
        if (capacity == null || capacity < 0) {
            throw new IllegalArgumentException("Capacity must be a positive number");
        }
        reservationsPerDay.putIfAbsent(day, new ConcurrentHashMap<>());
        reservationsPerDay.get(day).putIfAbsent(attractionName, new AttractionReservations());

        if (reservationsPerDay.get(day).get(attractionName).getCapacity() != 0) {
            throw new IllegalArgumentException("Capacity already set");
        }
        reservationsPerDay.get(day).get(attractionName).setCapacity(capacity);
    }
}
