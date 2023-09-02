package ar.edu.itba.pod.server.models;

import java.time.LocalDate;
import java.util.List;

public class AttractionSlot {
    private String attractionName;
    private LocalDate date;
    private int slotDuration;
    private int capacity;
    private List<Pass> passes;
}
