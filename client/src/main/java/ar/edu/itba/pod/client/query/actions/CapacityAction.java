package ar.edu.itba.pod.client.query.actions;

import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.serializers.table.specific.CapacityQueryTableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

public class CapacityAction extends QueryClientAction {
    private static final Logger logger = LoggerFactory.getLogger(CapacityAction.class);

    public CapacityAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run(Clients clients) throws IOException {
        // TODO: Implement
        var writer = Files.newOutputStream(getOutPath());
        var tableWriter = new CapacityQueryTableWriter(new OutputStreamWriter(writer));
        // Placeholder data
        tableWriter.addRow(600, 30, "Test Attraction 1");
        tableWriter.addRow(660, 25, "Test Attraction 2");
        tableWriter.close();
    }

    @Override
    public String toString() {
        return String.format("CapacityAction %s", super.toString());
    }
}
