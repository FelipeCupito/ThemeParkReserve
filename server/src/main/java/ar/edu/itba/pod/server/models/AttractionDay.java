package ar.edu.itba.pod.server.models;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class AttractionDay {
    private final Integer soltNumber;
    private boolean hasCapacity; //solo se debe cambiar en setCapacity
    private final List<AttractionSlot> slots = new ArrayList<>();

    public AttractionDay(Integer soltNumber) {
        this.soltNumber = soltNumber;
        this.hasCapacity = false;


        for (int i = 0; i < soltNumber; i++) {
            slots.add(new AttractionSlot());
        }
    }

    public void setCapacity(Integer capacity){
        //TODO: debe ser thead safe y bloquear el acceso a la lista de slots
        if (capacity < 0) {
            throw new IllegalArgumentException("Attraction capacity must be greater than 0");
        }

        if (hasCapacity){
            throw new IllegalArgumentException("Attraction capacity already set");
        }
        hasCapacity = true;

        Queue<Reservation> to_reassign = new ArrayDeque<>();

        //confirmo las reservas que pueda y agrego las que no pueda a la cola de reasignar
        for (int i = 0; i < soltNumber; i++) {
            //seteo la capacidad de cada slot y agrego las reservas que se puedan
            to_reassign = slots.get(i).setCapacity(capacity, to_reassign);
        }

        //si todavía tengo reservas por reasignar, las cancelo
        while(!to_reassign.isEmpty()){
            to_reassign.poll().cancel();
        }

    }

}
