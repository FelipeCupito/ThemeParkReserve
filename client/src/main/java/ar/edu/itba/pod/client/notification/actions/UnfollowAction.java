package ar.edu.itba.pod.client.notification.actions;

import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnfollowAction extends NotificationClientAction {
    private static final Logger logger = LoggerFactory.getLogger(UnfollowAction.class);

    public UnfollowAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run() {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("UnfollowAction %s", super.toString());
    }
}
