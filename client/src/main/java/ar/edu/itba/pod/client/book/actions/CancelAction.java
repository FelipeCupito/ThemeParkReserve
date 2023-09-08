package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CancelAction extends ReservationAction {
    private static final Logger logger = LoggerFactory.getLogger(CancelAction.class);

    public CancelAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("CancelAction %s", super.toString());
    }
}
