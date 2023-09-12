package ar.edu.itba.pod.client.admin.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.serializers.DayOfYearSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Park;

public class SlotsAction implements ClientAction {
    private static final Logger logger = LoggerFactory.getLogger(SlotsAction.class);

    String rideName;
    int dayOfYear;
    int amount;

    public SlotsAction(String rideName, int dayOfYear, int amount) {
        this.rideName = rideName;
        this.dayOfYear = dayOfYear;
        this.amount = amount;
    }

    public SlotsAction(PropertyManager properties) throws PropertyException {
        // TODO: check if parameter names are correct according to spec (spec is inconsistent)
        rideName = properties.getProperty("rideName");
        dayOfYear = properties.getDayOfYearProperty("dayOfYear");
        amount = properties.getPositiveIntProperty("amount");
    }

    @Override
    public void run(Clients clients) {
        clients.getAdminService().addSlotCapacity(
                Park.SlotCapacityRequest.newBuilder()
                        .setAttractionName(rideName)
                        .setDay(dayOfYear)
                        .setCapacity(amount)
                        .build()
        );
        logger.info("Loaded capacity of {} for SpaceMountain on day {}", amount, new DayOfYearSerializer().serialize(dayOfYear));
        // TODO: Get confirmed, relocations and cancelled
        logger.info("{} bookings confirmed without changes", 0);
        logger.info("{} bookings relocated", 0);
        logger.info("{} bookings cancelled", 0);
    }

    public String getRideName() {
        return rideName;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("SlotsAction { rideName: \"%s\", dayOfYear: %d, amount: %d }", rideName, dayOfYear,
                amount);
    }
}
