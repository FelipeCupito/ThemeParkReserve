package ar.edu.itba.pod.server.models;

import java.util.ArrayList;
import java.util.List;

public class AttractionDay {
    private final Integer soltNumber;
    private final List<AttractionSlot> slots;

    public AttractionDay(Integer soltNumber) {
        this.soltNumber = soltNumber;

        slots = new ArrayList<>(soltNumber);
        for (int i = 0; i < soltNumber; i++) {
            slots.set(i, new AttractionSlot());
        }

    }
}
