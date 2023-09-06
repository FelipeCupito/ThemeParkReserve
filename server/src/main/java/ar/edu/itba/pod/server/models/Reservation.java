package ar.edu.itba.pod.server.models;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Reservation implements Comparable<Reservation> {
    private final Pass pass;
    private final LocalDate date;
    private final Status status;

    public Reservation(Pass pass, Status status) {
        this.pass = pass;
        this.date = LocalDate.now();
        this.status = status;
    }

    @Override
    public int compareTo(Reservation o) {
        if (this.equals(o)) {
            return 0;
        }
        return this.date.compareTo(o.date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reservation that = (Reservation) o;

        return pass.equals(that.pass);
    }

    @Override
    public int hashCode() {
        return pass.hashCode();
    }
}
