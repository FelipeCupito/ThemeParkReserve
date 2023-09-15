package ar.edu.itba.pod.client.query.actions;

import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.serializers.table.specific.CapacityQueryTableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Park;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CapacityAction extends QueryClientAction {
    private static final Logger logger = LoggerFactory.getLogger(CapacityAction.class);

    public CapacityAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run(Clients clients) throws IOException {
        var suggestedCapacity = clients.getQueryService().getSuggestedCapacity(
                Park.Day.newBuilder()
                        .setDay(getDay())
                        .build()
        ).getSlotsList();

        if (suggestedCapacity.isEmpty()) {
            System.out.println("There are no attractions.");
            return;
        }

        var writer = Files.newOutputStream(getOutPath());
        var tableWriter = new CapacityQueryTableWriter(new OutputStreamWriter(writer));

        List<Park.SuggestedCapacitySlot> rows = new ArrayList<>(suggestedCapacity);
        rows.sort(Comparator.comparing(Park.SuggestedCapacitySlot::getSuggestedCapacity)
                .reversed()
                .thenComparing(Park.SuggestedCapacitySlot::getAttractionName));

        for (Park.SuggestedCapacitySlot row : rows) {
            tableWriter.addRow(row.getSlot(), row.getSuggestedCapacity(), row.getAttractionName());
        }

        tableWriter.close();

        System.out.printf("Output written to \"%s\".\n", getOutPath());
    }

    @Override
    public String toString() {
        return String.format("CapacityAction %s", super.toString());
    }
}
