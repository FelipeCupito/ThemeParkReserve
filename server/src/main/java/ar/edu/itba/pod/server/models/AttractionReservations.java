package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AttractionReservations {
    private final ConcurrentLinkedQueue<Reservation> reservations = new ConcurrentLinkedQueue<>();
    private Integer capacity = 0;
    private ReadWriteLock setUpCapacityLock = new ReentrantReadWriteLock(true);

    public void addReservation(Reservation reservation) throws IllegalArgumentException {
        setUpCapacityLock.writeLock().lock();
        try {
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation cannot be null");
            }
            if (reservations.contains(reservation)) {
                throw new IllegalArgumentException("Reservation already exists");
            }
            reservations.add(reservation);
        } finally {
            setUpCapacityLock.writeLock().unlock();
        }
    }

    public synchronized Park.ReservationType addReservationIfCapacityIsNotExceeded(Reservation reservation) throws IllegalArgumentException {
        setUpCapacityLock.writeLock().lock();
        try {
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
            if (capacity == 0) {
                reservation.setStatus(Park.ReservationType.RESERVATION_PENDING);
            } else {
                reservation.setStatus(Park.ReservationType.RESERVATION_CONFIRMED);
            }
            return reservation.getStatus();
        } finally {
            setUpCapacityLock.writeLock().unlock();
        }
    }

    public void confirmReservation(Reservation reservation) throws IllegalArgumentException {
        setUpCapacityLock.readLock().lock();
        try {
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
        } finally {
            setUpCapacityLock.readLock().unlock();
        }
    }

    public synchronized void confirmReservationIfCapacityIsNotExceeded(Reservation reservation) throws IllegalArgumentException {
        setUpCapacityLock.readLock().lock();
        try {
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
        } finally {
            setUpCapacityLock.readLock().unlock();
        }
    }

    public void cancelReservation(Reservation reservation) throws IllegalArgumentException {
        setUpCapacityLock.readLock().lock();
        try {
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation cannot be null");
            }
            if (!reservations.contains(reservation)) {
                throw new IllegalArgumentException("Reservation does not exist");
            }
            reservations.remove(reservation);
        } finally {
            setUpCapacityLock.readLock().unlock();
        }
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

    public int getTotalConfirmedReservationsBySlot(Integer openTime) {
        return (int) reservations.stream()
                .filter(r -> r.getStatus() == Park.ReservationType.RESERVATION_CONFIRMED && r.getOpenTime().equals(openTime))
                .count();
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

    public CapcitySetStats confirmAndMoveReservations(Integer endTime) {
        setUpCapacityLock.writeLock().lock();
        try {
            int confirmed = 0;
            int moved = 0;
            int cancelled = 0;

            Map<Integer, List<Reservation>> groupedReservations = this.getReservations().stream().collect(Collectors.groupingBy(Reservation::getOpenTime));

            // First confirm the ones that are on time
            for (Integer openTime : groupedReservations.keySet().stream().sorted().toList()) { // keySet() doesn't sort them
                int capacity = this.getCapacity();
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
            List<Reservation> pendingReservations = this.getReservations().stream().filter(reservation -> reservation.getStatus() == Park.ReservationType.RESERVATION_PENDING).toList();


            // Extremely slow operation ahead. Discretion is advised

            // For each one, try to move it to the next available slot
            for (Reservation res : pendingReservations) {
                int duration = res.getDuration();
                int openTime = res.getOpenTime();
                while (openTime <= endTime) {
                    int finalOpenTime = openTime;
                    long totalConfirmed = this.getReservations().stream().filter(r -> r.getOpenTime() == finalOpenTime).count();
                    if (totalConfirmed < this.getCapacity()) {
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
                    this.reservations.remove(res);
                }
            }

            return new CapcitySetStats(confirmed, moved, cancelled);
        } finally {
            setUpCapacityLock.writeLock().unlock();
        }
    }
}
