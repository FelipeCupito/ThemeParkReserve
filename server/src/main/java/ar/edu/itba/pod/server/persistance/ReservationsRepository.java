package ar.edu.itba.pod.server.persistance;

import ar.edu.itba.pod.server.models.AttractionReservations;
import ar.edu.itba.pod.server.models.CapacitySetStats;
import ar.edu.itba.pod.server.models.Reservation;
import ar.edu.itba.pod.server.services.NotificationService;
import services.Park;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ReservationsRepository {
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, AttractionReservations>> reservationsPerDay = new ConcurrentHashMap<>();

    public Park.ReservationType addReservationIfCapacityIsNotExceeded(Reservation reservation) throws IllegalArgumentException {
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
        return reservationsPerDay.get(day).get(name).addReservationIfCapacityIsNotExceeded(reservation);
    }

    public void cancelReservation(Reservation reservation, NotificationService notificationService) throws IllegalArgumentException {
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
        reservationsPerDay.get(day).get(name).cancelReservation(reservation, notificationService);
    }

    public void confirmReservationIfCapacityIsNotExceeded(Reservation reservation, NotificationService notificationService) throws IllegalArgumentException {
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

        if (!reservationsPerDay.containsKey(day)) {
            throw new IllegalArgumentException("Invalid reservation for that day");
        }

        if (!reservationsPerDay.get(day).containsKey(name)) {
            throw new IllegalArgumentException("Invalid reservation for that attraction");
        }
        reservationsPerDay.get(day).get(name).confirmReservationIfCapacityIsNotExceeded(reservation, notificationService);
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

    public synchronized List<Reservation> getConfirmedReservations(Integer day, String attractionName) throws IllegalArgumentException {
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        if (attractionName == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }
        reservationsPerDay.putIfAbsent(day, new ConcurrentHashMap<>());
        reservationsPerDay.get(day).putIfAbsent(attractionName, new AttractionReservations());
        return reservationsPerDay.get(day).get(attractionName).getConfirmedReservations();
    }

    public int getTotalReservations(Integer day, String attractionName) throws IllegalArgumentException {
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        if (attractionName == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }
        reservationsPerDay.putIfAbsent(day, new ConcurrentHashMap<>());
        reservationsPerDay.get(day).putIfAbsent(attractionName, new AttractionReservations());
        return reservationsPerDay.get(day).get(attractionName).getTotalReservations();
    }

    public int getTotalConfirmedReservationsBySlot(Integer day, String attractionName, Integer openTime) throws IllegalArgumentException {
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        if (attractionName == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }
        reservationsPerDay.putIfAbsent(day, new ConcurrentHashMap<>());
        reservationsPerDay.get(day).putIfAbsent(attractionName, new AttractionReservations());
        return reservationsPerDay.get(day).get(attractionName).getTotalConfirmedReservationsBySlot(openTime);
    }

    public synchronized CapacitySetStats setCapacity(Integer day, String attractionName, Integer capacity, Integer endTime, NotificationService notificationService) {
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
        reservationsPerDay.get(day).get(attractionName).setCapacity(capacity, notificationService);
        return reservationsPerDay.get(day).get(attractionName).confirmAndMoveReservations(endTime, notificationService);

    }

    public int getCapacity(Integer day, String attractionName) {
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        if (attractionName == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }

        if (!reservationsPerDay.containsKey(day) || !reservationsPerDay.get(day).containsKey(attractionName)) {
            return 0;
        }
        reservationsPerDay.putIfAbsent(day, new ConcurrentHashMap<>());
        reservationsPerDay.get(day).putIfAbsent(attractionName, new AttractionReservations());
        return reservationsPerDay.get(day).get(attractionName).getCapacity();
    }

    public int totalReservationsByUser(Park.UUID userId, Integer day) {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        if (day == null || day <= 1 || day > 365) {
            return 0;
        }

        int total = 0;
        for (String attractionName : reservationsPerDay.get(day).keySet()) {
            total += reservationsPerDay.get(day).get(attractionName).getReservations().stream().filter(reservation -> reservation.getUserId().equals(userId)).count();
        }
        return total;
    }
}
