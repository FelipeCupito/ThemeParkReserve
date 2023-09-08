package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import services.Park;

abstract class ReservationAction implements ClientAction {
    Park.UUID visitor;
    int day;
    String attraction;
    int slot;

    public ReservationAction(PropertyManager properties) throws PropertyException {
        // TODO: Check argument naming
        visitor = properties.getUUIDProperty("visitor");
        day = properties.getDayOfYearProperty("day");
        attraction = properties.getProperty("attraction");
        slot = properties.getTimeProperty("slot");
    }

    @Override
    public String toString() {
        return String.format("{ visitor: \"%s\", day: %d, attraction: \"%s\", slot: %d }", visitor, day, attraction,
                slot);
    }
}
