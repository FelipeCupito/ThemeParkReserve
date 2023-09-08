package ar.edu.itba.pod.server.models;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AttractionDay {
    private final Integer soltNumber;
    private boolean capacityFlag; //solo se debe cambiar en setCapacity
    private final List<AttractionSlot> slots = new ArrayList<>();
    private final ReadWriteLock capacityLock = new ReentrantReadWriteLock(true);  //por orden de llegada

    public AttractionDay(Integer soltNumber) {
        this.soltNumber = soltNumber;
        this.capacityFlag = false;

        for (int i = 0; i < soltNumber; i++) {
            slots.add(new AttractionSlot());
        }
    }

    private void setCapacityFlag(){
        if (capacityFlag){
            throw new IllegalArgumentException("Attraction capacity already set");
        }
        capacityFlag = true;
    }

    synchronized public void setCapacity(Integer capacity){
        //TODO: debe ser thead safe y bloquear el acceso a la lista de slots para las add reservas
        Utils.checkCapacity(capacity);

        setCapacityFlag();

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

    synchronized public Status addReservation(Pass pass, Integer slotIndex){
        checkIndex(slotIndex);

        //se bloquea el acceso a la lista de slots cuando se esta setieando la capacidad de este dia
        capacityLock.writeLock().lock();
        try{
            return slots.get(slotIndex).addReservation(pass);

        }finally {
            capacityLock.writeLock().unlock();
        }
    }

    public SlotAvailabilityResponse getSingleSlotAvailability(Integer slotIndex) {
        checkIndex(slotIndex);

        return slots.get(slotIndex).getSingleSlotAvailability();
    }

    public void confirmReservation(Pass pass, int slotIndex) {
        checkIndex(slotIndex);
        slots.get(slotIndex).confirmReservation(pass);

    }

    public void cancelReservation(Pass pass, int slotIndex) {
        checkIndex(slotIndex);
        slots.get(slotIndex).cancelReservation(pass);
    }

    private void checkIndex(int index){
        if(index < 0 || index >= soltNumber){
            throw new IllegalArgumentException("Invalid slot index");
        }
    }

}
