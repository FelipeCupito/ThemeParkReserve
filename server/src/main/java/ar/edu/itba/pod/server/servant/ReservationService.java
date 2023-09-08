package ar.edu.itba.pod.server.servant;

import ar.edu.itba.pod.server.models.Attraction;
import ar.edu.itba.pod.server.models.ParkRepository;
import ar.edu.itba.pod.server.models.SlotAvailabilityResponse;
import ar.edu.itba.pod.server.models.Status;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import services.Park;
import services.ReservationServiceGrpc;

import java.util.List;

public class ReservationService extends ReservationServiceGrpc.ReservationServiceImplBase {
    private final ParkRepository parkRepository;

    public ReservationService(ParkRepository parkRepository) {
        this.parkRepository = parkRepository;
    }

    @Override
    public void getAttractions(Empty request, StreamObserver<Park.AttractionList> responseObserver) {
        try{
            final List<Attraction> attractions =  parkRepository.getAttractions();

            responseObserver.onNext(Attraction.getParkAttractionList(attractions));
            responseObserver.onCompleted();

        }catch (Exception e){
            responseObserver.onError(e);
        }
    }

    @Override
    public void getSingleSlotAvailability(Park.Slot request, StreamObserver<Park.SlotCapacityResponse> responseObserver) {
        try{
            final SlotAvailabilityResponse response =  parkRepository.getSingleSlotAvailability(request.getAttractionName(), request.getDay(), request.getMinutes());

            responseObserver.onNext(response.getResponse());
            responseObserver.onCompleted();

        }catch (Exception e){
            responseObserver.onError(e);
        }

    }

    @Override
    public void getAttractionSlotsAvailabilityByRange(Park.SlotRange request, StreamObserver<Park.SlotCapacityResponseList> responseObserver) {
        try{
            final List<SlotAvailabilityResponse> responses =  parkRepository.getSlotsAvailabilityByRange(request.getAttractionName(), request.getDay(), request.getStartMinutes(), request.getEndMinutes());

            responseObserver.onNext(SlotAvailabilityResponse.getResponseList(responses));
            responseObserver.onCompleted();

        }catch (Exception e){
            responseObserver.onError(e);
        }
    }

    @Override
    public void addReservation(Park.ReservationRequest request, StreamObserver<Park.ReservationResponse> responseObserver) {
        try{
            final Status responses = parkRepository.addReservation(request.getPassId().getValue(), request.getSlot().getAttractionName(), request.getSlot().getDay(), request.getSlot().getMinutes());

            responseObserver.onNext(Park.ReservationResponse.newBuilder()
                    .setPassId(request.getPassId())
                    .setSlot(request.getSlot())
                    .setReservationType(Status.getReservationType(responses))
                    .build());
            responseObserver.onCompleted();

        }catch (Exception e){
            responseObserver.onError(e);
        }
    }

    @Override
    public void confirmReservation(Park.ReservationRequest request, StreamObserver<Empty> responseObserver) {
        try{
            parkRepository.confirmReservation(request.getPassId().getValue(), request.getSlot().getAttractionName(), request.getSlot().getDay(), request.getSlot().getMinutes());

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();

        }catch (Exception e){
            responseObserver.onError(e);
        }
    }

    @Override
    public void cancelReservation(Park.ReservationRequest request, StreamObserver<Empty> responseObserver) {
        try{
            parkRepository.cancelReservation(request.getPassId().getValue(), request.getSlot().getAttractionName(), request.getSlot().getDay(), request.getSlot().getMinutes());

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();

        }catch (Exception e){
            responseObserver.onError(e);
        }
    }
}
