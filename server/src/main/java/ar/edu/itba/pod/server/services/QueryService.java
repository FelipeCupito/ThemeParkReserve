package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.server.models.Attraction;
import ar.edu.itba.pod.server.models.Reservation;
import ar.edu.itba.pod.server.persistance.AttractionRepository;
import ar.edu.itba.pod.server.persistance.ReservationsRepository;
import services.Park;
import services.QueryServiceGrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryService extends QueryServiceGrpc.QueryServiceImplBase {
    private final ReservationsRepository reservationsRepository;
    private final AttractionRepository attractionRepository;

    public QueryService(ReservationsRepository reservationsRepository, AttractionRepository attractionRepository) {
        this.reservationsRepository = reservationsRepository;
        this.attractionRepository = attractionRepository;
    }

    @Override
    public void getSuggestedCapacity(services.Park.Day request, io.grpc.stub.StreamObserver<services.Park.SuggestedCapacitySlotList> responseObserver) {
        int day = request.getDay();
        if (day < 1 || day > 365) {
            responseObserver.onError(new IllegalArgumentException("Day must be a number between 1 and 365"));
            return;
        }

        List<String> attractions = attractionRepository.getAttractions().stream().map(Attraction::name).toList();
        List<Park.SuggestedCapacitySlot> slots = new ArrayList<>();

        for (String attraction : attractions) {
            Map<Integer, List<Reservation>> groupedReservations = reservationsRepository.getReservations(day, attraction).stream()
                    .collect(Collectors.groupingBy(Reservation::getOpenTime));
            int maxReservationsOpenTime = attractionRepository.getAttraction(attraction).startTime();
            int maxReservations = 0;
            for (Integer openTime : groupedReservations.keySet()) {
                if (groupedReservations.get(openTime).size() == 0) {
                    continue;
                }
                int reservations = groupedReservations.get(openTime).size();
                if (reservations > maxReservations) {
                    maxReservations = reservations;
                    maxReservationsOpenTime = openTime;
                }
            }
            Park.SuggestedCapacitySlot suggestedCapacitySlot = Park.SuggestedCapacitySlot.newBuilder()
                    .setAttractionName(attraction)
                    .setSlot(maxReservationsOpenTime)
                    .setSuggestedCapacity(maxReservations)
                    .build();
            slots.add(suggestedCapacitySlot);
        }

        slots.sort((o1, o2) -> {
            if (o1.getSuggestedCapacity() == o2.getSuggestedCapacity()) {
                return o1.getAttractionName().compareTo(o2.getAttractionName());
            }
            return o2.getSuggestedCapacity() - o1.getSuggestedCapacity();
        });

        Park.SuggestedCapacitySlotList suggestedCapacitySlotList = Park.SuggestedCapacitySlotList.newBuilder()
                .addAllSlots(slots)
                .build();

        responseObserver.onNext(suggestedCapacitySlotList);
        responseObserver.onCompleted();
    }

    @Override
    public void getConfirmedReservations(services.Park.Day request, io.grpc.stub.StreamObserver<services.Park.ReservationInfoList> responseObserver) {
        int day = request.getDay();
        if (day < 1 || day > 365) {
            responseObserver.onError(new IllegalArgumentException("Day must be a number between 1 and 365"));
            return;
        }

        List<String> attractionNames = attractionRepository.getAttractions().stream().map(Attraction::name).toList();
        List<Reservation> allReservations = new ArrayList<>();

        for (String name : attractionNames) {
            List<Reservation> reservations = reservationsRepository.getConfirmedReservations(day, name);
            allReservations.addAll(reservations);
        }

        allReservations.sort( (o1, o2) -> o1.getConfirmationOrder().compareTo(o2.getConfirmationOrder()));
        Park.ReservationInfoList reservationInfoList = Park.ReservationInfoList.newBuilder()
                .addAllReservations(allReservations.stream().map((res) -> {
                    return Park.ReservationInfo.newBuilder()
                            .setDay(res.getDay())
                            .setSlot(res.getOpenTime())
                            .setAttractionName(res.getAttractionName())
                            .setUserId(res.getUserId())
                            .setType(res.getStatus())
                            .build();
                }).toList())
                .build();

        responseObserver.onNext(reservationInfoList);
        responseObserver.onCompleted();
    }

}
