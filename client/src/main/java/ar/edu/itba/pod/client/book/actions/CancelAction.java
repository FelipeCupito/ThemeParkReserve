package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.serializers.DayOfYearSerializer;
import ar.edu.itba.pod.client.serializers.TimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Park;

public class CancelAction extends ReservationAction {
    private static final Logger logger = LoggerFactory.getLogger(CancelAction.class);

    public CancelAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run(Clients clients) {
        clients.getReservationService().cancelReservation(
                Park.ReservationInfo.newBuilder()
                        .setUserId(visitor)
                        .setDay(day)
                        .setAttractionName(attraction)
                        .setSlot(slot)
                        .build()
        );

        System.out.printf(
                "The reservation for %s at %s on the day %s was %s\n",
                attraction,
                new TimeSerializer().serialize(slot),
                new DayOfYearSerializer().serialize(day),
                "CANCELLED"
        );
    }

    @Override
    public String toString() {
        return String.format("CancelAction %s", super.toString());
    }
}
