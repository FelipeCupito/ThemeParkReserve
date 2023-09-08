package ar.edu.itba.pod.server.models;

import lombok.Getter;

@Getter
public class Reservation{
    private final Pass pass;
    private Status status;

    public Reservation(Pass pass){
        this.pass = pass;
    }

    synchronized Reservation makeReservation(Status status){
        pass.addReservation();
        this.status = status;
        return this;
    }

    synchronized Reservation cancelReservation(){
        if(!(status == Status.CANCELLED)){
            pass.removeReservation();
        }
        return this;
    }

    public void confirm(){
        status = Status.CONFIRMED;
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
