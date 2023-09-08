package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfirmAction extends ReservationAction {
    private static Logger logger = LoggerFactory.getLogger(ConfirmAction.class);

    public ConfirmAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("ConfirmAction %s", super.toString());
    }
}
