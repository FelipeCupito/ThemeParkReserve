package ar.edu.itba.pod.client.query.actions;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfirmedAction extends QueryClientAction {
    private static Logger logger = LoggerFactory.getLogger(ConfirmedAction.class);

    public ConfirmedAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("ConfirmedAction %s", super.toString());
    }
}
