package ar.edu.itba.pod.server.servant;


import ar.edu.itba.pod.server.models.ParkRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import services.Park;
import services.ParkAdminServiceGrpc;

public class AdminService extends ParkAdminServiceGrpc.ParkAdminServiceImplBase {

    private final ParkRepository parkRepository;

    public AdminService(ParkRepository parkRepository) {
        this.parkRepository = parkRepository;
    }

    @Override
    public void addAttraction(Park.Attraction request, StreamObserver<Empty> responseObserver) {
        try{
            parkRepository.addAttraction(request.getName(), request.getOpenTime(), request.getCloseTime(), request.getMinutesPerSlot());
        }catch (Exception e){
            responseObserver.onError(e);
        }

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void addPass(Park.Pass request, StreamObserver<Empty> responseObserver) {
        try {
            parkRepository.addPass(request.getId().getValue(), request.getType(), request.getDay());
        }catch (Exception e){
            responseObserver.onError(e);
        }

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void addSlotCapacity(Park.SlotCapacity request, StreamObserver<Park.SlotCapacityResponse> responseObserver) {
        try {
            parkRepository.setAttractionCapacity(request.getAttractionName(), request.getCapacity(), request.getDay());
        }catch (Exception e){
            responseObserver.onError(e);
        }

        //TODO: hay que generar un metodo que devuelva la capacidad de un slot
        responseObserver.onNext(Park.SlotCapacityResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
