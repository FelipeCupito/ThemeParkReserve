package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.server.Utils;
import ar.edu.itba.pod.server.services.NotificationService;
import services.Park;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AttractionReservations {
    private final ConcurrentLinkedQueue<Reservation> reservations = new ConcurrentLinkedQueue<>();
    private Integer capacity = 0;
    private final ReadWriteLock setUpCapacityLock = new ReentrantReadWriteLock(true);

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

    public synchronized void confirmReservationIfCapacityIsNotExceeded(Reservation reservation, NotificationService notificationService) throws IllegalArgumentException {
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
                    notificationService.sendNotification(r.getAttractionName(), r.getDay(), r.getUserId(), String.format("The reservation for %s at %s on the day %d has been CONFIRMED.",
                            r.getAttractionName(),
                            Utils.minutesToTime(r.getOpenTime()),
                            r.getDay()
                            ));
                    if (this.reservations.stream().noneMatch(res -> res.getStatus() == Park.ReservationType.RESERVATION_PENDING)) {
                        notificationService.disconnectUser(r.getUserId(), r.getAttractionName(), r.getDay());
                    }
                    return;
                }
            }
            throw new IllegalArgumentException("Reservation does not exist");
        } finally {
            setUpCapacityLock.readLock().unlock();
        }
    }

    public void cancelReservation(Reservation reservation, NotificationService notificationService) throws IllegalArgumentException {
        setUpCapacityLock.readLock().lock();
        try {
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation cannot be null");
            }
            if (!reservations.contains(reservation)) {
                throw new IllegalArgumentException("Reservation does not exist");
            }
            reservations.remove(reservation);
            notificationService.sendNotification(reservation.getAttractionName(), reservation.getDay(), reservation.getUserId(), String.format("The reservation for %s at %s on the day %d is CANCELLED.",
                    reservation.getAttractionName(),
                    Utils.minutesToTime(reservation.getOpenTime()),
                    reservation.getDay()));
            // Check if there are other pending reservations
            if (this.reservations.stream().noneMatch(res -> res.getStatus() == Park.ReservationType.RESERVATION_PENDING)) {
                notificationService.disconnectUser(reservation.getUserId(), reservation.getAttractionName(), reservation.getDay());
            }
            notificationService.disconnectUser(reservation.getUserId(), reservation.getAttractionName(), reservation.getDay());
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

    public synchronized void setCapacity(Integer capacity, NotificationService notificationService) {
        if (capacity == null || capacity < 0) {
            throw new IllegalArgumentException("Capacity must be a positive number");
        }
        if (this.capacity != 0) {
            throw new IllegalArgumentException("Capacity already set");
        }
        this.capacity = capacity;
        Set<Park.UUID> userIds = new HashSet<>();
        CompletableFuture.runAsync(() -> { // Run another thread to avoid blocking the main thread for notifications
            reservations.forEach(reservation -> {
                    String attractionName = reservation.getAttractionName();
                    int day = reservation.getDay();
                    notificationService.sendNotification(attractionName, day, reservation.getUserId(), String.format("%s announced slot capacity for the day %d: %d places.", attractionName, day, capacity));
            });
        });
    }

    public Integer getCapacity() {
        return capacity;
    }

    public CapacitySetStats confirmAndMoveReservations(Integer endTime, NotificationService notificationService) {
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
                    notificationService.sendNotification(reservation.getAttractionName(), reservation.getDay(), reservation.getUserId(), String.format("The reservation for %s at %s on the day %d is CONFIRMED.",
                            reservation.getAttractionName(),
                            Utils.minutesToTime(reservation.getOpenTime()),
                            reservation.getDay()));
                    confirmed++;
                    capacity--;
                }
            }

            // Get the pending ones
            // They are in order because the Reservations list keeps the order of request time
            List<Reservation> pendingReservations = this.getReservations().stream().filter(reservation -> reservation.getStatus() == Park.ReservationType.RESERVATION_PENDING).toList();


            // Extremely slow operation ahead. Discretion is advised

            Set<Park.UUID> pendingUsers = new HashSet<>();

            // For each one, try to move it to the next available slot
            for (Reservation res : pendingReservations) {
                int duration = res.getDuration();
                int openTime = res.getOpenTime();
                while (openTime < endTime) {
                    int finalOpenTime = openTime;
                    long totalReservationInSlot = this.getReservations().stream().filter(r -> r.getOpenTime() == finalOpenTime).count();
                    if (totalReservationInSlot < this.getCapacity()) {
                        int prevOpenTime = res.getOpenTime();
                        res.setOpenTime(openTime);
                        notificationService.sendNotification(res.getAttractionName(), res.getDay(), res.getUserId(), String.format("The reservation for %s at %s on the day %d was moved to %s and is PENDING.",
                                res.getAttractionName(),
                                Utils.minutesToTime(prevOpenTime),
                                res.getDay(),
                                Utils.minutesToTime(res.getOpenTime())));
                        moved++;
                        pendingUsers.add(res.getUserId());
                        break;
                    }
                    openTime += duration;
                }
                if (openTime >= endTime) {
                    res.setStatus(Park.ReservationType.RESERVATION_UNKNOWN);
                }

            }


            // Finally cancel the ones that couldn't be moved and remove them from the AttractionReservation
            for (Reservation res : pendingReservations.stream().filter(reservation -> reservation.getStatus() == Park.ReservationType.RESERVATION_UNKNOWN).toList()) {
                if (res.getStatus() == Park.ReservationType.RESERVATION_PENDING) {
                    cancelled++;
                    notificationService.sendNotification(res.getAttractionName(), res.getDay(), res.getUserId(), String.format("The reservation for %s at %s on the day %d has been CANCELLED.",
                            res.getAttractionName(),
                            Utils.minutesToTime(res.getOpenTime()),
                            res.getDay()));
                    pendingUsers.add(res.getUserId());
                }
            }

            // Lastly, close the notification connections of all the users that are not in the pending set
            Set<Park.UUID> nonPendingUsers = this.getReservations().stream().map(Reservation::getUserId).filter(userId -> !pendingUsers.contains(userId)).collect(Collectors.toSet());
            if (nonPendingUsers.size() > 0 && this.capacity > 0) {
                for (Park.UUID userId : nonPendingUsers) {
                    notificationService.disconnectUser(userId, this.getReservations().get(0).getAttractionName(), this.getReservations().get(0).getDay());
                }
            }
            return new CapacitySetStats(confirmed, moved, cancelled);
        } finally {
            setUpCapacityLock.writeLock().unlock();
        }
    }
}
