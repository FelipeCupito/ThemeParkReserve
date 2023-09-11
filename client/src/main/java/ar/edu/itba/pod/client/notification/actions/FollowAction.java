package ar.edu.itba.pod.client.notification.actions;

import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FollowAction extends NotificationClientAction {
    private static final Logger logger = LoggerFactory.getLogger(FollowAction.class);

    public FollowAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run(Clients clients) {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("FollowAction %s", super.toString());
    }
}
