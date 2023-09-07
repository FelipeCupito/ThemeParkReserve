package ar.edu.itba.pod.server.models;

import services.Park;

enum PassType {
    UNLIMITED {
        @Override
        public boolean canReserve(Pass pass) {
            return false;
        }
    },
    THREE {
        @Override
        public boolean canReserve(Pass pass) {
            return false;
        }
    },
    HALF_DAY {
        @Override
        public boolean canReserve(Pass pass) {
            return false;
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

    //TODO: faltaria pasarle la reserva que esta definida en el .proto
    abstract public boolean canReserve(Pass pass );
}