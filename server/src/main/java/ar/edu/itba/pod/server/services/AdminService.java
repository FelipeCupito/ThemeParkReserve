package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.server.models.Attraction;
import ar.edu.itba.pod.server.models.CapacitySetStats;
import ar.edu.itba.pod.server.persistance.AttractionRepository;
import ar.edu.itba.pod.server.persistance.PassRepository;
import ar.edu.itba.pod.server.persistance.ReservationsRepository;
import services.AdminServiceGrpc;
import services.Park;

public class AdminService extends AdminServiceGrpc.AdminServiceImplBase {
    private final AttractionRepository attractionRepository;
    private final PassRepository passRepository;
    private final ReservationsRepository reservationsRepository;
    private final NotificationService notificationService;

    public AdminService(AttractionRepository attractionRepository, PassRepository passRepository, ReservationsRepository reservationsRepository, NotificationService notificationService) {
        this.attractionRepository = attractionRepository;
        this.passRepository = passRepository;
        this.reservationsRepository = reservationsRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void addAttraction(Park.AttractionInfo attractionInfo, io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
        try {
            Attraction attraction = new Attraction(attractionInfo.getName(), attractionInfo.getOpenTime(), attractionInfo.getCloseTime(), attractionInfo.getSlotDuration());
            attractionRepository.addAttraction(attraction);
            responseObserver.onNext(com.google.protobuf.Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void addPass(Park.PassRequest passRequest, io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
        Park.PassType passType = passRequest.getType();
        Park.UUID userId = passRequest.getUserId();
        Integer day = passRequest.getDay();

        try {
            passRepository.addPass(userId, day, passType);
            responseObserver.onNext(com.google.protobuf.Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void addSlotCapacity(services.Park.SlotCapacityRequest request, io.grpc.stub.StreamObserver<Park.ReservationsResponse> responseObserver) {
        try {
            String attractionName = request.getAttractionName();
            if (!this.attractionRepository.attractionExists(attractionName)) {
                throw new IllegalArgumentException("Attraction does not exist");
            }
            Integer capacity = request.getCapacity();
            Integer day = request.getDay();
            Integer endTime = this.attractionRepository.getAttraction(attractionName).endTime();
            CapacitySetStats stats = this.reservationsRepository.setCapacity(day, attractionName, capacity, endTime, notificationService);
            Park.ReservationsResponse response = Park.ReservationsResponse.newBuilder()
                    .setConfirmed(stats.confirmed())
                    .setCancelled(stats.cancelled())
                    .setMoved(stats.moved())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

}
