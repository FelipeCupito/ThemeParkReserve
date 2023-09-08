package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.PropertyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvailabilityAction implements ClientAction {
    private static Logger logger = LoggerFactory.getLogger(AvailabilityAction.class);

    int day;
    String attraction;
    int slot;
    Integer slotTo;

    public AvailabilityAction(PropertyManager properties) throws PropertyException {
        // TODO: Check argument naming
        day = properties.getDayOfYearProperty("day");
        slot = properties.getTimeProperty("slot");
        try {
            attraction = properties.getProperty("attraction");
        } catch (PropertyNotFoundException e) {
            // Optional property
            attraction = null;
        }
        try {
            slotTo = properties.getTimeProperty("slotTo");
        } catch (PropertyNotFoundException e) {
            // Optional property
            slotTo = null;
        }
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("AvailabilityAction { day: %d, attraction: \"%s\", slot: %d, slotTo: %d }", day,
                attraction, slot, slotTo);
    }
}
