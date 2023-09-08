package ar.edu.itba.pod.client.query.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;

import java.nio.file.Path;

abstract class QueryClientAction implements ClientAction {
    int day;
    Path outPath;

    public QueryClientAction(PropertyManager properties) throws PropertyException {
        // TODO: Check argument naming
        day = properties.getDayOfYearProperty("day");
        outPath = Path.of(properties.getProperty("outPath"));
    }

    @Override
    public String toString() {
        return String.format("{ day: %d, outPath: \"%s\" }", day, outPath);
    }
}
