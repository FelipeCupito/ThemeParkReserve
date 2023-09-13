package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.server.models.Reservation;
import ar.edu.itba.pod.server.persistance.AttractionRepository;
import ar.edu.itba.pod.server.persistance.PassRepository;
import ar.edu.itba.pod.server.persistance.ReservationsRepository;
import io.grpc.stub.StreamObserver;
import services.NotificationServiceGrpc;
import services.Park;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationService extends NotificationServiceGrpc.NotificationServiceImplBase {
    private final ConcurrentHashMap<Map.Entry<String, Integer>, ConcurrentHashMap<Park.UUID, StreamObserver<Park.NotificationResponse>>>  userStreamObservers = new ConcurrentHashMap<>();
    private final AttractionRepository attractionRepository;
    private final PassRepository passRepository;
    private final ReservationsRepository reservationsRepository;

    public NotificationService(AttractionRepository attractionRepository, PassRepository passRepository, ReservationsRepository reservationsRepository) {
        this.attractionRepository = attractionRepository;
        this.passRepository = passRepository;
        this.reservationsRepository = reservationsRepository;
    }

    @Override
    public void registerUser(services.Park.NotificationRequest request, io.grpc.stub.StreamObserver<services.Park.NotificationResponse> responseObserver) {
        String attractionName = request.getName();
        int day = request.getDay();
        Park.UUID userId = request.getUserId();
        if (checkValidRequest(responseObserver, attractionName, day, userId, this.attractionRepository, passRepository))
            return;

        List<Reservation> userReservations = reservationsRepository.getReservations(day, attractionName).stream().filter(reservation -> reservation.getUserId().equals(userId)).toList();
        if (userReservations.size() == 0) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("User has not a valid reservation for that attraction on that day").asRuntimeException());
            return;
        }

        Map.Entry<String, Integer> key = new AbstractMap.SimpleEntry<>(attractionName, day);
        userStreamObservers.putIfAbsent(key, new ConcurrentHashMap<>());
        var dayObservers = userStreamObservers.get(key);

        if (dayObservers.putIfAbsent(userId, responseObserver) != null) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("User already registered for that attraction on that day").asRuntimeException());
            return;
        }

        boolean anyPending = false;

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("User registered for notifications on '").append(attractionName).append("' reservation on day ").append(day).append(".\n");
        for (Reservation reservation : userReservations) {
            messageBuilder.append("The reservation for '").append(reservation.getAttractionName()).append("' on day ").append(reservation.getDay()).append(" is ");
            if (reservation.getStatus() == Park.ReservationType.RESERVATION_CONFIRMED) {
                messageBuilder.append("CONFIRMED");
            } else {
                messageBuilder.append("PENDING");
            }
            messageBuilder.append(".\n");

            if (reservation.getStatus() == Park.ReservationType.RESERVATION_PENDING) {
                anyPending = true;
            }
        }

        responseObserver.onNext(Park.NotificationResponse.newBuilder()
                .setMessage(messageBuilder.toString())
                .build());

        if (!anyPending) {
            disconnectUser(userId, attractionName, day);
        }
    }



    @Override
    public void unregisterUser(services.Park.NotificationRequest request, io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
        String attractionName = request.getName();
        int day = request.getDay();
        Park.UUID userId = request.getUserId();
        if (checkValidRequest(responseObserver, attractionName, day, userId, this.attractionRepository, passRepository)) {
            return;
        }

        Map.Entry<String, Integer> key = new AbstractMap.SimpleEntry<>(attractionName, day);
        if (!userStreamObservers.containsKey(key)) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("User not registered for that attraction on that day").asRuntimeException());
            return;
        }
        ConcurrentHashMap<Park.UUID, StreamObserver<Park.NotificationResponse>> observerMap = userStreamObservers.get(key);

        var userObserver = observerMap.remove(userId);
        if (userObserver == null) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("User not registered for that attraction on that day").asRuntimeException());
            return;
        }

        userObserver.onCompleted();

        responseObserver.onNext(com.google.protobuf.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    private static boolean checkValidRequest(StreamObserver responseObserver, String attractionName, int day, Park.UUID userId, AttractionRepository attractionRepository, PassRepository passRepository) {
        if (day <= 1 || day > 365) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("Day must be a number between 1 and 365").asRuntimeException());
            return true;
        }
        if (!attractionRepository.attractionExists(attractionName)) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("Attraction does not exist").asRuntimeException());
            return true;
        }
        if (!passRepository.passExists(userId, day)) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("User has not a valid pass for that day").asRuntimeException());
            return true;
        }
        return false;
    }

    public void sendNotification(String attractionName, int day, Park.UUID userId, String message) {
        Map.Entry<String, Integer> key = new AbstractMap.SimpleEntry<>(attractionName, day);
        if (!userStreamObservers.containsKey(key)) {
            return;
        }
        if (!userStreamObservers.get(key).containsKey(userId)) {
            return;
        }
        StreamObserver<Park.NotificationResponse> responseObserver = userStreamObservers.get(key).get(userId);
        Park.NotificationResponse response = Park.NotificationResponse.newBuilder()
                .setMessage(message)
                .build();
        responseObserver.onNext(response);
    }

    public void disconnectUser(Park.UUID userId, String attractionName, int day) {
        Map.Entry<String, Integer> key = new AbstractMap.SimpleEntry<>(attractionName, day);
        if (!userStreamObservers.containsKey(key)) {
            return;
        }
        if (!userStreamObservers.get(key).containsKey(userId)) {
            return;
        }
        StreamObserver<Park.NotificationResponse> responseObserver = userStreamObservers.get(key).get(userId);
        responseObserver.onCompleted();
    }
}
