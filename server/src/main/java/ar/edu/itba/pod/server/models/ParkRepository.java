package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParkRepository {
    private final Map<String, Attraction> attractions = new HashMap<>();
    private final Map<UUID, Pass> passes = new HashMap<>();
    private final ReadWriteLock attractionLock = new ReentrantReadWriteLock(true);  //por orden de llegada
    private final ReadWriteLock passLock = new ReentrantReadWriteLock(true);  //por orden de llegada

    ////////////////////
    //  Admin Service //
    ////////////////////
    /**
     * Adds a new attraction to the park
     */
    public void addAttraction(String name, int openTime, int closeTime, int minutesPerSlot) {
        attractionLock.writeLock().lock();
        try{
            Attraction newAttraction = new Attraction(name, openTime, closeTime, minutesPerSlot);
            //Check if attraction already exists
            if(attractions.containsKey(name)){
                throw new IllegalArgumentException("Attraction already exists");
            }
            attractions.put(name, newAttraction);
        } finally {
            attractionLock.writeLock().unlock();
        }
    }

    /**
     * Adds a new pass to the park
     */
    public void addPass(String uuidString, Park.PassType type, int day) {
        passLock.writeLock().lock();
        try{
            Pass newPass = new Pass(uuidString, type, day);

            if (passes.containsKey(newPass.getUuid())) {
                throw new IllegalArgumentException("Pass already exists");
            }

            passes.put(newPass.getUuid(), newPass);
        }finally {
            passLock.writeLock().unlock();
        }
    }

    /**
     * Sets the capacity of an attraction
     */
    public void setAttractionCapacity(String attractionName, Integer capacity, Integer day) {
        //get attraction
        Attraction attraction = getAttractionIfExist(attractionName);

        attraction.setDayCapacity(day, capacity);
    }

    //////////////////////////
    //  Reservation Service //
    /////////////////////////
    public List<Attraction> getAttractions() {
        //para mi no hace falta que sea thread safe
        return new ArrayList<>(attractions.values());
    }
    public SlotAvailabilityResponse getSingleSlotAvailability(String attractionName, Integer day, Integer minutes) {
        //get attraction
        Attraction attraction = getAttractionIfExist(attractionName);
        //calculate slot capacity
        return attraction.getSingleSlotAvailability(day, minutes);
    }

    public List<SlotAvailabilityResponse> getSlotsAvailabilityByRange(String attractionName, Integer day, Integer startMinutes, Integer endMinutes) {

        //get attraction
        Attraction attraction = getAttractionIfExist(attractionName);
        //calculate slot capacity
        return attraction.getSlotsAvailabilityByRange(day, startMinutes, endMinutes);
    }

    public Status addReservation(String uuidString, String attractionName, Integer day, Integer minutes) {
        //get attraction
        Attraction attraction = getAttractionIfExist(attractionName);
        //get pass
        Pass pass = getPassIfExist(uuidString);

        //check pass
        if(!pass.canReserve(day, minutes)){
            throw new IllegalArgumentException("Pass is not available for this day");
        }
        //add reservation
        return attraction.addReservation(pass, day, minutes);

    }

    public void confirmReservation(String uuidString, String attractionName, Integer day, Integer minutes) {
        //get attraction
        Attraction attraction = getAttractionIfExist(attractionName);
        //get pass
        Pass pass = getPassIfExist(uuidString);
        //check pass
        if(!pass.isValid(day, minutes)){
            throw new IllegalArgumentException("Pass is not available for this day");
        }

        attraction.confirmReservation(pass, day, minutes);
    }

    public void cancelReservation(String uuidString, String attractionName, Integer day, Integer minutes) {
        //get attraction
        Attraction attraction = getAttractionIfExist(attractionName);
        //get pass
        Pass pass = getPassIfExist(uuidString);
        //check pass
        if(!pass.isValid(day, minutes)){
            throw new IllegalArgumentException("Pass is not available for this day");
        }

        attraction.cancelReservation(pass, day, minutes);
    }

    ///////////////
    //  Private  //
    //////////////
    private void checkAttractionName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Attraction name cannot be null or empty");
        }
    }

    private Attraction getAttractionIfExist(String name) {
        Utils.checkAttractionName(name);
        Attraction attraction = attractions.get(name);
        if (attraction == null) {
            throw new IllegalArgumentException("Attraction does not exist");
        }
        return attraction;
    }

    private Pass getPassIfExist(String uuidString){
        UUID id = UUID.fromString(uuidString);
        Pass pass = passes.get(id);
        if (pass == null) {
            throw new IllegalArgumentException("Pass does not exist");
        }
        return pass;
    }
}