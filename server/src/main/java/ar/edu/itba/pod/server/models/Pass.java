package ar.edu.itba.pod.server.models;

import lombok.Getter;
import services.Park;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Pass {
    private static final int DAY_OF_YEAR = 365;
    private final UUID uuid;
    private final PassType type;
    private final int day;
    private Integer reservationNumber = 0;

    public Pass(String uuidString, Park.PassType type, int day) {
        if (uuidString == null || type == null) {
            throw new IllegalArgumentException("Null arguments are not allowed");
        }
        if(day < 1 || day > DAY_OF_YEAR) {
            throw new IllegalArgumentException("Invalid day");
        }

        this.uuid = UUID.fromString(uuidString);
        this.type = PassType.fromParkPassType(type);
        this.day = day;
    }

    public void addReservation() {
        //TODO: esto debe ser atomica junto con la reserva del slot
        reservationNumber++;
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

    public boolean canReserve(Integer day, Integer minutes) {
        return type.canReserve(this, day, minutes);
    }

    public boolean isValid(Integer day, Integer minutes) {
        return type.isValid(this, day, minutes);
    }
}
