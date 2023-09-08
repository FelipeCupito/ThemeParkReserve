package ar.edu.itba.pod.server.models;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Reservation{
    private final Pass pass;
    //private final Integer day;
    private Status status;

    public Reservation(Pass pass, Status status) {
        this.pass = pass;
        //this.day = day;
        this.status = status;
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

    public void confirm() {
        this.status = Status.CONFIRMED;
    }

   public void cancel() {
        this.status = Status.CANCELLED;
    }

    public void pending() {
        this.status = Status.PENDING;
    }

}
