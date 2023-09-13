package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.server.persistance.AttractionRepository;
import ar.edu.itba.pod.server.persistance.PassRepository;
import io.grpc.stub.StreamObserver;
import services.NotificationServiceGrpc;
import services.Park;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationService extends NotificationServiceGrpc.NotificationServiceImplBase {
    private final ConcurrentHashMap<Map.Entry<String, Integer>, ConcurrentHashMap<Park.UUID, StreamObserver<Park.NotificationResponse>>>  userStreamObservers = new ConcurrentHashMap<>();
    private final AttractionRepository attractionRepository;
    private final PassRepository passRepository;

    public NotificationService(AttractionRepository attractionRepository, PassRepository passRepository) {
        this.attractionRepository = attractionRepository;
        this.passRepository = passRepository;
    }

    @Override
    public void registerUser(services.Park.NotificationRequest request, io.grpc.stub.StreamObserver<services.Park.NotificationResponse> responseObserver) {
        String attractionName = request.getName();
        int day = request.getDay();
        Park.UUID userId = request.getUserId();
        if (checkValidRequest(responseObserver, attractionName, day, userId, this.attractionRepository, passRepository))
            return;
        Map.Entry<String, Integer> key = new AbstractMap.SimpleEntry<>(attractionName, day);
        userStreamObservers.putIfAbsent(key, new ConcurrentHashMap<>());
        var dayObservers = userStreamObservers.get(key);

        if (dayObservers.putIfAbsent(userId, responseObserver) != null) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("User already registered for that attraction on that day").asRuntimeException());
            return;
        }
        responseObserver.onNext(Park.NotificationResponse.newBuilder()
                .setMessage("User registered for notifications on '" + attractionName + "' reservation on day " + day + ".")
                .build());
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
