package ar.edu.itba.pod.server.models;

import java.util.ArrayList;
import java.util.List;

public class AttractionSlot {

    private Integer capacity = null;

    //ordenada por fecha de reserva
    private List<Reservation> reservations = new ArrayList<>();
}
