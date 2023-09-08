package ar.edu.itba.pod.client.query.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;

abstract class QueryClientAction implements ClientAction {
    int day;

    public QueryClientAction(PropertyManager properties) throws PropertyException {
        // TODO: Check argument naming
        day = properties.getDayOfYearProperty("day");
    }

    @Override
    public String toString() {
        return String.format("{ day: %d }", day);
    }
}
