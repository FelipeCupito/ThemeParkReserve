package ar.edu.itba.pod.client.query.actions;

import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.serializers.table.specific.ConfirmedQueryTableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Park;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

public class ConfirmedAction extends QueryClientAction {
    private static final Logger logger = LoggerFactory.getLogger(ConfirmedAction.class);

    public ConfirmedAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run(Clients clients) throws IOException {
        var confirmedBookings = clients.getQueryService()
                .getConfirmedReservations(
                        Park.Day.newBuilder()
                                .setDay(getDay())
                                .build()
                )
                .getReservationsList();

        var writer = Files.newOutputStream(getOutPath());
        var tableWriter = new ConfirmedQueryTableWriter(new OutputStreamWriter(writer));

        for (var row : confirmedBookings) {
            tableWriter.addRow(row.getOpenTime(), row.getUserId(), row.getAttractionName());
        }

        tableWriter.close();
    }

    @Override
    public String toString() {
        return String.format("ConfirmedAction %s", super.toString());
    }
}
