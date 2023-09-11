package ar.edu.itba.pod.server.persistance;

import ar.edu.itba.pod.server.models.Attraction;

import java.util.List;
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

    public Attraction getAttraction(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Attraction name cannot be null");
        }
        for (Attraction a : attractions) {
            if (a.name().equals(name)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Attraction does not exist");
    }

    public List<Attraction> getAttractions() {
        return attractions.stream().toList();
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
