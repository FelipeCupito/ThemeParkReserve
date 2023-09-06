package ar.edu.itba.pod.server.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attraction {
    private String name;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private int slotDuration;

    private final List<AttractionSlot> slots = new ArrayList<>(365);
}
