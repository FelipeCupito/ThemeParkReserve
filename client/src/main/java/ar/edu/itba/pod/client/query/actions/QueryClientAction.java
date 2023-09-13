package ar.edu.itba.pod.client.query.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;

import java.nio.file.Path;

abstract class QueryClientAction implements ClientAction {
    private int day;
    private Path outPath;

    public QueryClientAction(PropertyManager properties) throws PropertyException {
        day = properties.getDayOfYearProperty("day");
        outPath = Path.of(properties.getProperty("outPath"));
    }

    @Override
    public String toString() {
        return String.format("{ day: %d, outPath: \"%s\" }", day, outPath);
    }

    public int getDay() {
        return day;
    }

    public Path getOutPath() {
        return outPath;
    }
}
