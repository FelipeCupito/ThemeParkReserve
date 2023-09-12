package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.serializers.TimeSerializer;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttractionsAction implements ClientAction {
    private static final Logger logger = LoggerFactory.getLogger(AttractionsAction.class);

    @Override
    public void run(Clients clients) {
        // FIXME: Check output format (doesn't seem to be specified)
        System.out.println("Attractions:");
        var attractions = clients.getReservationService()
                .getAttractions(Empty.newBuilder().build())
                .getAttractionsList();

        for (var attraction : attractions) {
            System.out.printf("name: %s | open: %s | close: %s | slot: %d\n",
                    attraction.getName(),
                    new TimeSerializer().serialize(attraction.getOpenTime()),
                    new TimeSerializer().serialize(attraction.getCloseTime()),
                    attraction.getSlotDuration()
            );
        }
    }

    @Override
    public String toString() {
        return "AttractionsAction {}";
    }
}
