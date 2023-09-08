package ar.edu.itba.pod.client.query.actions;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapacityAction extends QueryClientAction {
    private static Logger logger = LoggerFactory.getLogger(CapacityAction.class);

    public CapacityAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("CapacityAction %s", super.toString());
    }
}
