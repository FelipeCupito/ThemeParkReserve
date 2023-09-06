package ar.edu.itba.pod.server.models;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Pass {
    private UUID id;
    private PassType type;
    private LocalDate date;
    private Integer reservationNumber = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pass pass = (Pass) o;

        return Objects.equals(id, pass.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
