package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.server.models.Attraction;
import ar.edu.itba.pod.server.models.Reservation;
import ar.edu.itba.pod.server.persistance.AttractionRepository;
import ar.edu.itba.pod.server.persistance.PassRepository;
import ar.edu.itba.pod.server.persistance.ReservationsRepository;
import services.Park;
import services.ReservationServiceGrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReservationService extends ReservationServiceGrpc.ReservationServiceImplBase {
    private final AttractionRepository attractionRepository;
    private final PassRepository passRepository;
    private final ReservationsRepository reservationsRepository;
    private final NotificationService notificationService;

    public ReservationService(AttractionRepository attractionRepository, PassRepository passRepository, ReservationsRepository reservationsRepository, NotificationService notificationService) {
        this.attractionRepository = attractionRepository;
        this.passRepository = passRepository;
        this.reservationsRepository = reservationsRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void getAttractions(com.google.protobuf.Empty request, io.grpc.stub.StreamObserver<services.Park.AttractionInfoList> responseObserver) {
        Park.AttractionInfoList.Builder attractionInfoList = Park.AttractionInfoList.newBuilder();
        for (Attraction attraction : this.attractionRepository.getAttractions()) {
            Park.AttractionInfo attractionInfo = Park.AttractionInfo.newBuilder()
                    .setName(attraction.name())
                    .setOpenTime(attraction.startTime())
                    .setCloseTime(attraction.endTime())
                    .setSlotDuration(attraction.minutesPerSlot())
                    .build();
            attractionInfoList.addAttractions(attractionInfo);
        }
        responseObserver.onNext(attractionInfoList.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getSlotAvailability(services.Park.SlotRequest request, io.grpc.stub.StreamObserver<services.Park.SlotAvailabilityInfo> responseObserver) {
        try {
            String attractionName = request.getAttractionName();
            int openTime = request.getSlot();
            int day = request.getDay();
            checkSlotRequestValues(attractionName, day, openTime);
            checkValidOpenTime(attractionName, openTime);

            List<Reservation> slotReservations = this.reservationsRepository.getReservations(day, attractionName).stream()
                    .filter(reservation -> reservation.getOpenTime().equals(openTime))
                    .toList();

            Park.SlotAvailabilityInfo slotAvailabilityInfo = this.buildSlotAvailabilityInfo(attractionName, day, openTime, slotReservations);
            responseObserver.onNext(slotAvailabilityInfo);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getSlotRangeAvailability(services.Park.SlotRangeRequest request, io.grpc.stub.StreamObserver<services.Park.SlotAvailabilityInfoList> responseObserver) {
        try {
            Park.SlotAvailabilityInfoList.Builder slotAvailabilityInfoList = Park.SlotAvailabilityInfoList.newBuilder();
            String name = request.getAttractionName();
            if (name.equals("")) {
                throw new IllegalArgumentException("Attraction name cannot be empty");
            }
            int day = request.getDay();
            int openTime1 = request.getSlot1();
            int openTime2 = request.getSlot2();
            checkSlotRequestValues(name, day, openTime1, openTime2);

            List<Reservation> reservations = this.reservationsRepository.getReservations(day, name).stream()
                    .filter(reservation -> reservation.getOpenTime() >= openTime1 && reservation.getOpenTime() <= openTime2)
                    .toList();

            Map<Integer, List<Reservation>> reservationsSeparated = reservations.stream()
                    .collect(Collectors.groupingBy(Reservation::getOpenTime));

            int attractionStartTime = this.attractionRepository.getAttraction(name).startTime();
            int attractionEndTime = this.attractionRepository.getAttraction(name).endTime();
            int minutesPerSlot = this.attractionRepository.getAttraction(name).minutesPerSlot();
            for (int i = attractionStartTime; i < attractionEndTime; i += minutesPerSlot) {
                if (i >= openTime1 && i <= openTime2) {
                    reservationsSeparated.putIfAbsent(i, new ArrayList<>());
                }
            }

            for (Integer openTime : reservationsSeparated.keySet()) {
                Park.SlotAvailabilityInfo slotAvailabilityInfo = buildSlotAvailabilityInfo(name, day, openTime, reservationsSeparated.get(openTime));
                slotAvailabilityInfoList.addSlots(slotAvailabilityInfo);
            }

            responseObserver.onNext(slotAvailabilityInfoList.build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAllSlotsRangeAvailability(services.Park.SlotRangeRequest request, io.grpc.stub.StreamObserver<services.Park.SlotAvailabilityInfoList> responseObserver) {
        try {

            Park.SlotAvailabilityInfoList.Builder slotAvailabilityInfoList = Park.SlotAvailabilityInfoList.newBuilder();
            List<String> names = this.attractionRepository.getAttractions().stream().map(Attraction::name).toList();
            int day = request.getDay();
            int openTime1 = request.getSlot1();
            int openTime2 = request.getSlot2();
            checkSlotRequestValues(day, openTime1, openTime2);
            for (String name : names) {
                List<Reservation> reservations = this.reservationsRepository.getReservations(day, name).stream()
                        .filter(reservation -> reservation.getOpenTime() >= openTime1 && reservation.getOpenTime() <= openTime2)
                        .toList();

                Map<Integer, List<Reservation>> reservationsSeparated = reservations.stream()
                        .collect(Collectors.groupingBy(Reservation::getOpenTime));

                int attractionStartTime = this.attractionRepository.getAttraction(name).startTime();
                int attractionEndTime = this.attractionRepository.getAttraction(name).endTime();
                int minutesPerSlot = this.attractionRepository.getAttraction(name).minutesPerSlot();
                for (int i = attractionStartTime; i < attractionEndTime; i += minutesPerSlot) {
                    if (i >= openTime1 && i <= openTime2) {
                        reservationsSeparated.putIfAbsent(i, new ArrayList<>());
                    }
                }

                for (Integer openTime : reservationsSeparated.keySet()) {
                    Park.SlotAvailabilityInfo slotAvailabilityInfo = buildSlotAvailabilityInfo(name, day, openTime, reservationsSeparated.get(openTime));
                    slotAvailabilityInfoList.addSlots(slotAvailabilityInfo);
                }
            }
            responseObserver.onNext(slotAvailabilityInfoList.build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void addReservation(services.Park.ReservationInfo request, io.grpc.stub.StreamObserver<Park.ReservationInfoType> responseObserver) {
        try {
            String attractionName = request.getAttractionName();
            int day = request.getDay();
            int openTime = request.getSlot();
            Park.UUID userId = request.getUserId();
            if (userId.getValue().equals("")) {
                throw new IllegalArgumentException("User id cannot be empty");
            }
            checkSlotRequestValues(attractionName, day, openTime);
            checkValidOpenTime(attractionName, openTime);
            checkIfPassIsValid(userId, day, openTime);

            Park.ReservationType reservationType = Park.ReservationType.RESERVATION_PENDING;

            int capacity = this.reservationsRepository.getCapacity(day, attractionName);
            if (capacity > 0) {
                if (this.reservationsRepository.getTotalConfirmedReservationsBySlot(day, attractionName, openTime) < capacity) {
                    reservationType = Park.ReservationType.RESERVATION_CONFIRMED;
                } else {
                    throw new IllegalArgumentException("Capacity exceeded");
                }
            }
            int duration = this.attractionRepository.getAttraction(attractionName).minutesPerSlot();
            Reservation reservation = new Reservation(attractionName, day, openTime, userId, reservationType, duration);
            Park.ReservationType type = this.reservationsRepository.addReservationIfCapacityIsNotExceeded(reservation);

            responseObserver.onNext(Park.ReservationInfoType.newBuilder().setType(type).build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void confirmReservation(services.Park.ReservationInfo request, io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
        try {
            String attractionName = request.getAttractionName();
            int day = request.getDay();
            int openTime = request.getSlot();
            Park.UUID userId = request.getUserId();
            if (userId.getValue().equals("")) {
                throw new IllegalArgumentException("User id cannot be empty");
            }
            checkSlotRequestValues(attractionName, day, openTime);
            checkValidOpenTime(attractionName, openTime);
            this.passRepository.getPassType(userId, day);
            if (this.reservationsRepository.getCapacity(day, attractionName) == 0) {
                throw new IllegalArgumentException("Capacity not set");
            }

            Integer duration = this.attractionRepository.getAttraction(attractionName).minutesPerSlot();
            Reservation reservation = new Reservation(attractionName, day, openTime, userId, Park.ReservationType.RESERVATION_CONFIRMED, duration);
            this.reservationsRepository.confirmReservationIfCapacityIsNotExceeded(reservation, notificationService);
            responseObserver.onNext(com.google.protobuf.Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override

    public void cancelReservation(services.Park.ReservationInfo request, io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
        try {
            String attractionName = request.getAttractionName();
            int day = request.getDay();
            int openTime = request.getSlot();
            Park.UUID userId = request.getUserId();
            if (userId.getValue().equals("")) {
                throw new IllegalArgumentException("User id cannot be empty");
            }
            checkSlotRequestValues(attractionName, day, openTime);
            checkValidOpenTime(attractionName, openTime);
            this.passRepository.getPassType(userId, day);

            Integer duration = this.attractionRepository.getAttraction(attractionName).minutesPerSlot();
            Reservation reservation = new Reservation(attractionName, day, openTime, userId, Park.ReservationType.RESERVATION_UNKNOWN, duration);
            this.reservationsRepository.cancelReservation(reservation, notificationService);
            responseObserver.onNext(com.google.protobuf.Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }



    private void checkSlotRequestValues(String attractionName, int day, int openTime) {
        if (!this.attractionRepository.attractionExists(attractionName)) {
            throw new IllegalArgumentException("Attraction does not exist");
        }
        if (day < 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        if (openTime < 0 || openTime > 1439) {
            throw new IllegalArgumentException("Open time must be a number between 0 and 1439");
        }
    }

    private void checkSlotRequestValues(String attractionName, int day, int openTime1, int openTime2) {
        if (!this.attractionRepository.attractionExists(attractionName)) {
            throw new IllegalArgumentException("Attraction does not exist");
        }
        this.checkSlotRequestValues(day, openTime1, openTime2);

    }

    private void checkSlotRequestValues(int day, int openTime1, int openTime2) {
        if (day < 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between 1 and 365");
        }
        if (openTime1 < 0 || openTime1 > 1439) {
            throw new IllegalArgumentException("Open time 1 must be a number between 0 and 1439");
        }
        if (openTime2 < 0 || openTime2 > 1439) {
            throw new IllegalArgumentException("Open time 2 must be a number between 0 and 1439");
        }
        if (openTime1 > openTime2) {
            throw new IllegalArgumentException("Open time 1 must be less than or equal to open time 2");
        }
    }

    private Park.SlotAvailabilityInfo buildSlotAvailabilityInfo(String attractionName, Integer day, Integer openTime, List<Reservation> slotReservations) {
        int capacity = this.reservationsRepository.getCapacity(day, attractionName);
        int confirmed = (int) slotReservations.stream()
                .filter(reservation -> reservation.getStatus().equals(Park.ReservationType.RESERVATION_CONFIRMED))
                .count();
        int pending = slotReservations.size() - confirmed;
        return Park.SlotAvailabilityInfo.newBuilder()
                .setAttractionName(attractionName)
                .setCapacity(capacity)
                .setConfirmed(confirmed)
                .setPending(pending)
                .setSlot(openTime)
                .build();
    }

    private void checkIfPassIsValid(Park.UUID userId, Integer day, Integer openTime) {
        if (!this.passRepository.passExists(userId, day)) {
            throw new IllegalArgumentException("Pass does not exist");
        }
        Park.PassType passType = this.passRepository.getPassType(userId, day);
        if (passType.equals(Park.PassType.PASS_UNKNOWN)) {
            throw new IllegalArgumentException("Pass type is unknown");
        }

        if (passType.equals(Park.PassType.PASS_HALF_DAY)) {
            if (openTime < 0 || openTime > 839) { // Before 14:00
                throw new IllegalArgumentException("Open time must be a number between 0 and 839");
            }
            return;
        }

        if (passType.equals(Park.PassType.PASS_THREE)) {
            int totalReservations = this.reservationsRepository.totalReservationsByUser(userId, day);
            if (totalReservations >= 3) {
                throw new IllegalArgumentException("User already has 3 reservations");
            }
        }
    }

    private void checkValidOpenTime(String attractionName, int openTime) {
        int startTime = this.attractionRepository.getAttraction(attractionName).startTime();
        int endTime = this.attractionRepository.getAttraction(attractionName).endTime();
        int minutesPerSlot = this.attractionRepository.getAttraction(attractionName).minutesPerSlot();
        if (openTime < startTime || openTime >= endTime) {
            throw new IllegalArgumentException("Slot time must be between attraction start time and end time");
        }
        if ((openTime - startTime) % minutesPerSlot != 0) {
            throw new IllegalArgumentException("Slot time must be a multiple of attraction slot duration");
        }
    }
}
