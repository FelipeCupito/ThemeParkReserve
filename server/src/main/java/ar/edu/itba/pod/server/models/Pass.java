package ar.edu.itba.pod.server.models;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Pass {
    private UUID id;
    private PassType type;
    private LocalDate date;
    private Integer reservationNumber = 0;
}
