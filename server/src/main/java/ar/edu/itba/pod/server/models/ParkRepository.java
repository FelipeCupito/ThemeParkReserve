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
            if(attractions.containsKey(name)) {
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
        Attraction attraction = attractions.get(attractionName);
        if (attraction == null) {
            throw new IllegalArgumentException("Attraction does not exist");
        }

        attraction.setDayCapacity(day, capacity);
    }
}