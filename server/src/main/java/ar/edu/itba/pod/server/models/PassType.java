package ar.edu.itba.pod.server.models;

import services.Park;

enum PassType {
    UNLIMITED {
        @Override
        public boolean canReserve(Pass pass) {
            return true;
        }

        @Override
        public boolean isValid(Pass pass, Integer day, Integer minutes) {
            Utils.checkDay(day);
            return pass.getDay() == day;
        }

    },
    THREE {
        @Override
        public boolean canReserve(Pass pass) {
            return pass.getReservationNumber() < 3;
        }

        @Override
        public boolean isValid(Pass pass, Integer day, Integer minutes) {
            Utils.checkDay(day);
            return pass.getDay() == day;
        }
    },
    HALF_DAY {
        @Override
        public boolean canReserve(Pass pass) {
            return true;
        }

        @Override
        public boolean isValid(Pass pass, Integer day, Integer minutes) {
            Utils.checkDay(day);
            Utils.checkMinutes(minutes);
            return pass.getDay() == day && minutes <= Utils.MINUTES_OF_DAY / 2;
        }
    };


    public static PassType fromParkPassType(Park.PassType p) {
        return switch (p) {
            case UNLIMITED -> UNLIMITED;
            case THREE -> THREE;
            case HALF_DAY -> HALF_DAY;
            default -> throw new IllegalArgumentException("Invalid pass type");
        };
    }

    //si pude reserva por cantidad de reservas
    abstract public boolean canReserve(Pass pass);

    //si es valido para el dia y el minuto
    abstract public boolean isValid(Pass pass, Integer day, Integer minutes);
}