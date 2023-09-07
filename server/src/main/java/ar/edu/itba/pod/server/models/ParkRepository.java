package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.*;

public class ParkRepository {
    private final Map<String, Attraction> attractions = new HashMap<>();
    private final Map<UUID, Pass> passes = new HashMap<>();

    ////////////////////
    //  Admin Service //
    ///////////////////
    /**
     * Adds a new attraction to the park
     */
    public void addAttraction(String name, int openTime, int closeTime, int minutesPerSlot) {
        Attraction newAttraction = new Attraction(name, openTime, closeTime, minutesPerSlot);

        //Check if attraction already exists
        if(attractions.containsKey(name)) {
            throw new IllegalArgumentException("Attraction already exists");
        }

        attractions.put(name, newAttraction);
    }

    /**
     * Adds a new pass to the park
     */
    public void addPass(String uuidString, Park.PassType type, int day) {
        Pass newPass = new Pass(uuidString, type, day);

        if (passes.containsKey(newPass.getUuid())) {
            throw new IllegalArgumentException("Pass already exists");
        }

        passes.put(newPass.getUuid(), newPass);
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