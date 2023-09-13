package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.serializers.TimeSerializer;
import ar.edu.itba.pod.client.serializers.table.ColumnAlignment;
import ar.edu.itba.pod.client.serializers.table.ColumnProperties;
import ar.edu.itba.pod.client.serializers.table.TableWriter;
import ar.edu.itba.pod.client.serializers.table.specific.AttractionsTableWriter;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class AttractionsAction implements ClientAction {
    private static final Logger logger = LoggerFactory.getLogger(AttractionsAction.class);

    @Override
    public void run(Clients clients) throws IOException {
        var attractions = clients.getReservationService()
                .getAttractions(Empty.newBuilder().build())
                .getAttractionsList();

        var writer = new AttractionsTableWriter(new OutputStreamWriter(System.out));

        for (var attraction : attractions) {
            writer.addRow(attraction.getOpenTime(), attraction.getCloseTime(), attraction.getName());
        }

        writer.close();
    }

    @Override
    public String toString() {
        return "AttractionsAction {}";
    }
}
