package ar.edu.itba.pod.server.models;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

//TODO: esta clase debe ser thread safe
public class AttractionSlot {
    private Integer capacity = null;

    //ordenada por fecha de reserva
    private final SortedSet<Reservation> reservations = new ConcurrentSkipListSet<>();
}
