package ar.edu.itba.pod.server.models;

import java.util.*;

public class AttractionSlot {
    private Integer capacity = null;
    private final List<Reservation> reservations = new ArrayList<>();

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
            //elimino las reservas que no se pudieron confirmar
            reservations.removeAll(to_reassign);
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
        checkIfExist(pass);
        //si no tiene capacidad, agrego la reserva como pendiente
        if(hasNotCapacity()){
            Reservation reservation = new Reservation(pass);
            reservations.add(reservation.makeReservation(Status.PENDING));
            return Status.PENDING;
        }
        //sino, si hay lugar agrego la reserva como confirmada
        if(capacity > reservations.size()){
            Reservation reservation = new Reservation(pass);
            reservations.add(reservation.makeReservation(Status.CONFIRMED));
            return Status.CONFIRMED;
        }
        //si no hay lugar lanzo una excepcion
        throw new IllegalArgumentException("No capacity");
    }

    public void confirmReservation(Pass pass) {
        if(hasNotCapacity()){
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
                reservation.cancelReservation();
                return;
            }
        }
        throw new IllegalArgumentException("Pass does not exist");
    }

    public Reservation getReservation(Pass pass) {
        for (Reservation reservation : reservations) {
            if(reservation.getPass().equals(pass)){
                return reservation;
            }
        }
        throw new IllegalArgumentException("Pass does not exist");
    }

    public boolean hasNotCapacity(){
        return capacity == null;
    }


    private void checkIfExist(Pass pass){
        for (Reservation reservation : reservations) {
            if(reservation.getPass().equals(pass)){
                throw new IllegalArgumentException("Pass already exist");
            }
        }
    }
}
