package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.properties.exceptions.PropertyNotFoundException;
import ar.edu.itba.pod.client.serializers.table.specific.AvailabilityTableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Park;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.stream.Stream;

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
        if (slotTo == null && attraction == null) {
            throw new PropertyException("slotTo, attraction", "Invalid combination");
        }
    }

    @Override
    public void run(Clients clients) throws IOException {
        Stream<Park.SlotAvailabilityInfo> rows;

        if (slotTo == null && attraction != null) {
            var response = clients.getReservationService()
                    .getSlotAvailability(Park.SlotRequest.newBuilder()
                            .setDay(day)
                            .setAttractionName(attraction)
                            .setOpenTime(slot)
                            .build());
            rows = Stream.of(response);
        } else if (slotTo != null && attraction != null) {
            var response = clients.getReservationService()
                    .getSlotRangeAvailability(Park.SlotRangeRequest.newBuilder()
                            .setDay(day)
                            .setAttractionName(attraction)
                            .setOpenTime1(slot)
                            .setOpenTime2(slotTo)
                            .build());

            rows = response.getSlotsList().stream();
        } else if (slotTo != null && attraction == null) {
            var response = clients.getReservationService()
                    .getAllSlotsRangeAvailability(Park.SlotRangeRequest.newBuilder()
                            .setDay(day)
                            .setOpenTime1(slot)
                            .setOpenTime2(slotTo)
                            .build());

            rows = response.getSlotsList().stream();
        } else {
            throw new IllegalStateException();
        }

        var tableWriter = new AvailabilityTableWriter(new OutputStreamWriter(System.out));

        rows.forEach((row) -> {
            try {
                tableWriter.addRow(row.getSlot(), row.getCapacity(), row.getPending(), row.getConfirmed(), row.getAttractionName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        tableWriter.close();
    }

    @Override
    public String toString() {
        return String.format("AvailabilityAction { day: %d, attraction: \"%s\", slot: %d, slotTo: %d }", day,
                attraction, slot, slotTo);
    }
}
