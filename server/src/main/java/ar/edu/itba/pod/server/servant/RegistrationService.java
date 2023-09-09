package ar.edu.itba.pod.server.servant;

import ar.edu.itba.pod.server.models.NotificationRepository;
import ar.edu.itba.pod.server.models.ParkRepository;
import services.Park;
import services.RegistrationServiceGrpc;

public class RegistrationService extends RegistrationServiceGrpc.RegistrationServiceImplBase {

    NotificationRepository notificationRepository;
    ParkRepository parkRepository;

    public RegistrationService(NotificationRepository notificationRepository, ParkRepository parkRepository) {
        this.notificationRepository = notificationRepository;
        this.parkRepository = parkRepository;
    }

    @Override
    public void registerUser(Park.NotificationRegistryRequest request, io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
//        try {
//            notificationRepository.addNotification(request.getUuid(), request.getSlot());
//        } catch (Exception e) {
//            responseObserver.onError(e);
//        }


        // Check if attraction Name exists
        String attractionName = request.getAttractionName();
        if (!parkRepository.attractionExists(attractionName)) {
            responseObserver.onError(new Exception("Attraction does not exist"));
        }
        // Check if day is valid
        if (request.getDay() < 0 || request.getDay() >= 365) {
            responseObserver.onError(new Exception("Day is not valid"));
        }
        // Check if pass is valid

        // Check if uuid is already registered for that slot


        responseObserver.onNext(com.google.protobuf.Empty.newBuilder().build());
        responseObserver.onCompleted();

    }
}
