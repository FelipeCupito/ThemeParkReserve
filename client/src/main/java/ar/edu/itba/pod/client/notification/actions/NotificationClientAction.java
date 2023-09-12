package ar.edu.itba.pod.client.notification.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import services.Park;

abstract class NotificationClientAction implements ClientAction {
    Park.UUID visitor;
    int day;
    String attraction;

    public NotificationClientAction(PropertyManager properties) throws PropertyException {
        // TODO: Check argument naming
        visitor = properties.getUUIDProperty("visitor");
        day = properties.getDayOfYearProperty("day");
        attraction = properties.getProperty("attraction");
    }

    @Override
    public String toString() {
        return String.format("{ visitor: \"%s\", day: %d, attraction: \"%s\" }", visitor, day, attraction);
    }
}
