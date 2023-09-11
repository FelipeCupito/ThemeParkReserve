package ar.edu.itba.pod.server.persistance;

import ar.edu.itba.pod.server.models.AttractionReservations;
import ar.edu.itba.pod.server.models.CapcitySetStats;
import ar.edu.itba.pod.server.models.Reservation;
import services.Park;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ReservationsRepository {
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, AttractionReservations>> reservationsPerDay = new ConcurrentHashMap<>();

    public void addReservationIfCapacityIsNotExceeded(Reservation reservation) throws IllegalArgumentException {
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
        reservationsPerDay.get(day).get(name).addReservationIfCapacityIsNotExceeded(reservation);
    }

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

        if (!reservationsPerDay.contains(day)) {
            throw new IllegalArgumentException("Invalid reservation for that day");
        }

        if (!reservationsPerDay.get(day).containsKey(name)) {
            throw new IllegalArgumentException("Invalid reservation for that attraction");
        }
        reservationsPerDay.get(day).get(name).confirmReservation(reservation);
    }

    public void confirmReservationIfCapacityIsNotExceeded(Reservation reservation) throws IllegalArgumentException {
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

        if (!reservationsPerDay.contains(day)) {
            throw new IllegalArgumentException("Invalid reservation for that day");
        }

        if (!reservationsPerDay.get(day).containsKey(name)) {
            throw new IllegalArgumentException("Invalid reservation for that attraction");
        }
        reservationsPerDay.get(day).get(name).confirmReservationIfCapacityIsNotExceeded(reservation);
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

    public int getTotalConfirmedReservations(Integer day, String attractionName) throws IllegalArgumentException {
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        if (attractionName == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }
        reservationsPerDay.putIfAbsent(day, new ConcurrentHashMap<>());
        reservationsPerDay.get(day).putIfAbsent(attractionName, new AttractionReservations());
        return reservationsPerDay.get(day).get(attractionName).getTotalConfirmedReservations();
    }

    public synchronized CapcitySetStats setCapacity(Integer day, String attractionName, Integer capacity, Integer endTime) {
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

        return this.confirmAndMoveReservations(reservationsPerDay.get(day).get(attractionName), endTime);

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

    private CapcitySetStats confirmAndMoveReservations(AttractionReservations attractionReservations, Integer endTime) {
        int confirmed = 0;
        int moved = 0;
        int cancelled = 0;

        Map<Integer, List<Reservation>> groupedReservations = attractionReservations.getReservations().stream().collect(Collectors.groupingBy(Reservation::getOpenTime));

        // First confirm the ones that are on time
        for (Integer openTime : groupedReservations.keySet().stream().sorted().toList()) { // keySet() doesn't sort them
            int capacity = attractionReservations.getCapacity();
            for (Reservation reservation : groupedReservations.get(openTime)) {
                if (capacity <= 0) {
                    break;
                }
                reservation.setStatus(Park.ReservationType.RESERVATION_CONFIRMED);
                confirmed++;
                capacity--;
            }
        }

        // Get the pending ones
        // They are in order because the Reservations list keeps the order of request time
        List<Reservation> pendingReservations = attractionReservations.getReservations().stream().filter(reservation -> reservation.getStatus() == Park.ReservationType.RESERVATION_PENDING).toList();


        // Extremely slow operation ahead. Discretion is advised

        // For each one, try to move it to the next available slot
        for (Reservation res : pendingReservations) {
            int duration = res.getDuration();
            int openTime = res.getOpenTime();
            while (openTime <= endTime) {
                int finalOpenTime = openTime;
                long totalConfirmed = attractionReservations.getReservations().stream()
                        .filter(r -> r.getOpenTime() == finalOpenTime)
                        .count();
                if (totalConfirmed < attractionReservations.getCapacity()) {
                    res.setOpenTime(openTime);
                    res.setStatus(Park.ReservationType.RESERVATION_CONFIRMED);
                    moved++;
                    break;
                }
                openTime += duration;
            }
        }

        // Finally cancel the ones that couldn't be moved and remove them from the AttractionReservation
        for (Reservation res : pendingReservations) {
            if (res.getStatus() == Park.ReservationType.RESERVATION_PENDING) {
                cancelled++;
                attractionReservations.cancelReservation(res);
            }
        }

        return new CapcitySetStats(confirmed, moved, cancelled);
    }
}
