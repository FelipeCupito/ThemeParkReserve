package ar.edu.itba.pod.server.models;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

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
}
