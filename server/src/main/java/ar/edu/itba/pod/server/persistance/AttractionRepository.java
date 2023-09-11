package ar.edu.itba.pod.server.persistance;

import ar.edu.itba.pod.server.models.Attraction;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AttractionRepository {
    private final ConcurrentLinkedQueue<Attraction> attractions = new ConcurrentLinkedQueue<>();

    public void addAttraction(Attraction attraction) {
        if (attraction == null) {
            throw new IllegalArgumentException("Attraction cannot be null");
        }

        if (attractions.contains(attraction)) {
            throw new IllegalArgumentException("Attraction already exists");
        }
        attractions.add(attraction);
    }

    public boolean attractionExists(String name) {
        if (name == null) {
            return false;
        }
        for (Attraction a : attractions) {
            if (a.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
