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
        rideName = properties.getProperty("ride");
        dayOfYear = properties.getDayOfYearProperty("day");
        amount = properties.getPositiveIntProperty("capacity");
    }

    @Override
    public void run(Clients clients) {
        var response = clients.getAdminService().addSlotCapacity(
                Park.SlotCapacityRequest.newBuilder()
                        .setAttractionName(rideName)
                        .setDay(dayOfYear)
                        .setCapacity(amount)
                        .build()
        );
        System.out.printf("Loaded capacity of %d for SpaceMountain on day %s\n", amount, new DayOfYearSerializer().serialize(dayOfYear));
        System.out.printf("%d bookings confirmed without changes\n", response.getConfirmed());
        System.out.printf("%d bookings relocated\n", response.getMoved());
        System.out.printf("%d bookings cancelled\n", response.getCancelled());
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
        return String.format("SlotsAction { ride: \"%s\", day: %d, capacity: %d }", rideName, dayOfYear,
                amount);
    }
}
