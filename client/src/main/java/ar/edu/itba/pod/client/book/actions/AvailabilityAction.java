package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.PropertyNotFoundException;
import ar.edu.itba.pod.client.serializers.table.specific.AvailabilityTableWriter;
import ar.edu.itba.pod.client.serializers.table.specific.CapacityQueryTableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class AvailabilityAction implements ClientAction {
    private static final Logger logger = LoggerFactory.getLogger(AvailabilityAction.class);

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
    public void run() throws IOException {
        // TODO: Implement
        var tableWriter = new AvailabilityTableWriter(new OutputStreamWriter(System.out));
        // Placeholder data
        tableWriter.addRow(600, 30, 5, 10, "Test Attraction 1");
        tableWriter.addRow(660, null, 25, 0, "Test Attraction 2");
        tableWriter.close();
    }

    @Override
    public String toString() {
        return String.format("AvailabilityAction { day: %d, attraction: \"%s\", slot: %d, slotTo: %d }", day,
                attraction, slot, slotTo);
    }
}
