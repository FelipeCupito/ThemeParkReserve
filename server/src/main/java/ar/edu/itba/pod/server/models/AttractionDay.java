package ar.edu.itba.pod.server.models;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AttractionDay {
    private final Integer soltNumber;
    private boolean capacityFlag = false; //solo se debe cambiar en setCapacity
    private final List<AttractionSlot> slots = new ArrayList<>();
    private final ReadWriteLock capacityLock = new ReentrantReadWriteLock(true);  //por orden de llegada

    public AttractionDay(Integer soltNumber) {
        this.soltNumber = soltNumber;

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
        Utils.checkCapacity(capacity);
        //se bloquea el acceso a la lista de slots para que no se agregue una nueva
        // reserva mientras se esta setieando la capacidad de este dia
        capacityLock.writeLock().lock();
        try{
            setCapacityFlag();
            Queue<Reservation> to_reassign = new ArrayDeque<>();

            //confirmo las reservas que pueda y agrego las que no pueda a la cola de reasignar
            for (int i = 0; i < soltNumber; i++) {
                //seteo la capacidad de cada slot y agrego las reservas que se puedan
                to_reassign = slots.get(i).setCapacity(capacity, to_reassign);
            }

            //si todavía tengo reservas por reasignar, las cancelo
            while(!to_reassign.isEmpty()){
                to_reassign.poll().cancelReservation();
            }

        }finally {
            capacityLock.writeLock().unlock();
        }
    }

    synchronized public Status addReservation(Pass pass, Integer slotIndex){
        //se bloquea el acceso a la lista de slots cuando se esta setieando la capacidad de este dia
        capacityLock.readLock().lock();
        try{
            checkIndex(slotIndex);
            return slots.get(slotIndex).addReservation(pass);

        }finally {
            capacityLock.readLock().lock();
        }
    }

    public SlotAvailabilityResponse getSingleSlotAvailability(Integer slotIndex) {
        //se bloquea el acceso a la lista de slots cuando se esta setieando la capacidad de este dia
        capacityLock.readLock().lock();
        try {
            checkIndex(slotIndex);
            return slots.get(slotIndex).getSingleSlotAvailability();
        }finally {
            capacityLock.readLock().unlock();
        }
    }

    public void confirmReservation(Pass pass, int slotIndex) {
        //se bloquea el acceso a la lista de slots cuando se esta setieando la capacidad de este dia
        capacityLock.readLock().lock();
        try{
            checkIndex(slotIndex);
            slots.get(slotIndex).confirmReservation(pass);
        }finally {
            capacityLock.readLock().unlock();
        }
    }

    public void cancelReservation(Pass pass, int slotIndex) {
        //se bloquea el acceso a la lista de slots cuando se esta setieando la capacidad de este dia
        capacityLock.readLock().lock();
        try{
            checkIndex(slotIndex);
            slots.get(slotIndex).cancelReservation(pass);
        }finally {
            capacityLock.readLock().unlock();
        }
    }

    public Reservation getReservation(int slotIndex, Pass pass) {
        //se bloquea el acceso a la lista de slots cuando se esta setieando la capacidad de este dia
        capacityLock.readLock().lock();
        try{
            checkIndex(slotIndex);
            return slots.get(slotIndex).getReservation(pass);
        }finally {
            capacityLock.readLock().unlock();
        }
    }

    private void checkIndex(int index){
        if(index < 0 || index >= soltNumber){
            throw new IllegalArgumentException("Invalid slot index");
        }
    }
}
