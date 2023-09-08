package ar.edu.itba.pod.server.models;

import java.util.*;

//TODO: esta clase debe ser thread safe
public class AttractionSlot {
    private Integer capacity = null;
    private final List<Reservation> reservations = new ArrayList<>();    //TODO: debe ser una lista atomica

    public Queue<Reservation> setCapacity(Integer capacity, Queue<Reservation> to_reassign){
        this.capacity = capacity;

        //primero confirmo todos los que tengo
        for(int i = 0; i < capacity && i < reservations.size(); i++){
            reservations.get(i).confirm();
        }

        // si todavía tengo capacidad agrego las reserva a reasignar
        if(capacity > reservations.size()){
            while(!to_reassign.isEmpty() && capacity > reservations.size()){
                reservations.add(to_reassign.poll());
            }
        }
        // si la capacidad es menor a la cantidad de reservas, agregamos las reservas a reasignar
        else{
            for(int i = capacity; i < reservations.size(); i++){
                to_reassign.add(reservations.get(i));
            }
        }
        return to_reassign;
    }

    public SlotAvailabilityResponse getSingleSlotAvailability() {
        SlotAvailabilityResponse response = new SlotAvailabilityResponse(capacity);
        for (Reservation reservation : reservations) {
            response.calculate(reservation);
        }
        return response;
    }

    public Status addReservation(Pass pass) {
        //check if exist
        if(checkIfExist(pass)){
            throw new IllegalArgumentException("Pass already exist");
        }

        if(!hasCapacity()){
            reservations.add(new Reservation(pass, Status.PENDING));
            return Status.PENDING;
        }

        if(capacity > reservations.size()){
            reservations.add(new Reservation(pass, Status.CONFIRMED));
            return Status.CONFIRMED;
        }

        throw new IllegalArgumentException("No capacity");
    }

    public void confirmReservation(Pass pass) {
        if(!hasCapacity()){
            throw new IllegalArgumentException("No capacity");
        }

        for (Reservation reservation : reservations) {
            if(reservation.getPass().equals(pass)){
                if(reservation.getStatus().equals(Status.CONFIRMED)){
                    throw new IllegalArgumentException("Pass already confirmed");
                }
                reservation.confirm();
                return;
            }
        }
        throw new IllegalArgumentException("Pass does not exist");
    }

    public void cancelReservation(Pass pass) {
        for (Reservation reservation : reservations) {
            if(reservation.getPass().equals(pass)){
                reservation.cancel();
                return;
            }
        }
        throw new IllegalArgumentException("Pass does not exist");
    }

    public boolean hasCapacity(){
        return capacity != null;
    }


    private Boolean checkIfExist(Pass pass){
        for (Reservation reservation : reservations) {
            if(reservation.getPass().equals(pass)){
                return true;
            }
        }
        return false;
    }

}
