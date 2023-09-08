package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.List;

public class SlotAvailabilityResponse {

    private final Integer capacity;
    private Integer confirmed = 0;
    private Integer cancelled = 0;
    private Integer pending = 0;

    public SlotAvailabilityResponse(Integer Capacity) {
        this.capacity = Capacity;
    }

    public void calculate(Reservation reservation){
        switch (reservation.getStatus()) {
            case CONFIRMED -> confirmed++;
            case CANCELLED -> cancelled++;
            case PENDING -> pending++;
        }
    }

    public Park.SlotCapacityResponse getResponse(){
        return Park.SlotCapacityResponse.newBuilder()
                .setCapacity(capacity == null ? -1 : capacity)
                .setConfirmed(confirmed)
                .setCancelled(cancelled)
                .setPending(pending)
                .build();
    }

    public static Park.SlotCapacityResponseList getResponseList(List<SlotAvailabilityResponse> responses){
        Park.SlotCapacityResponseList.Builder builder = Park.SlotCapacityResponseList.newBuilder();
        for (SlotAvailabilityResponse response : responses) {
            builder.addSlotCapacityResponses(response.getResponse());
        }
        return builder.build();
    }
}
