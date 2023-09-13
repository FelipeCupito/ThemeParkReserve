package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.serializers.DayOfYearSerializer;
import ar.edu.itba.pod.client.serializers.TimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Park;

public class BookAction extends ReservationAction {
    private static final Logger logger = LoggerFactory.getLogger(BookAction.class);

    public BookAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run(Clients clients) {
        var status = clients.getReservationService().addReservation(
                Park.ReservationInfo.newBuilder()
                        .setUserId(visitor)
                        .setDay(day)
                        .setAttractionName(attraction)
                        .setSlot(slot)
                        .build()
        );

        System.out.printf(
                "The reservation for %s at %s on the day %s is %s\n",
                attraction,
                new TimeSerializer().serialize(slot),
                new DayOfYearSerializer().serialize(day),
                switch (status.getType()) {
                    case RESERVATION_UNKNOWN, UNRECOGNIZED -> throw new IllegalStateException();
                    case RESERVATION_CONFIRMED -> "CONFIRMED";
                    case RESERVATION_PENDING -> "PENDING";
                }
        );
    }

    @Override
    public String toString() {
        return String.format("BookAction %s", super.toString());
    }
}
