package ar.edu.itba.pod.server.models;

import lombok.Getter;
import services.Park;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Pass {
    private final UUID uuid;
    private final PassType type;
    private final int day;
    private Integer reservationNumber;

    public Pass(String uuidString, Park.PassType type, Integer day) {
        this(uuidString, type, day, 0);
    }

    public Pass(String uuidString, Park.PassType type, int day, Integer reservationNumber) {
        Utils.checkString(uuidString);
        Utils.checkDay(day);
        if (type == null) {
            throw new IllegalArgumentException("Pass type cannot be null");
        }
        this.uuid = UUID.fromString(uuidString);
        this.type = PassType.fromParkPassType(type);
        this.day = day;
        this.reservationNumber = reservationNumber;
    }

    /**
     * @WARNING: this is not thread safe
     */
    public void addReservation() {
        this.reservationNumber++;
    }

    /**
     * @WARNING: this is not thread safe
     */
    public void removeReservation() {
        reservationNumber--;
    }

    // si puede hacer una nueva reserva
    public boolean canReserve() {
        return type.canReserve(this);
    }

    // si el pase es valido para ese dia y minuto
    public boolean isValid(Integer day, Integer minutes) {
        return type.isValid(this, day, minutes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pass pass = (Pass) o;

        return Objects.equals(uuid, pass.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
