package ar.edu.itba.pod.client.notification.actions;

import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Park;

public class UnfollowAction extends NotificationClientAction {
    private static final Logger logger = LoggerFactory.getLogger(UnfollowAction.class);

    public UnfollowAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run(Clients clients) {
        clients.getNotificationService().unregisterUser(
                Park.NotificationRequest.newBuilder()
                        .setName(attraction)
                        .setDay(day)
                        .setUserId(visitor)
                        .build()
        );

        System.out.printf("Unregister events from user \"%s\", attraction \"%s\" on day %d\n", visitor, attraction, day);
    }

    @Override
    public String toString() {
        return String.format("UnfollowAction %s", super.toString());
    }
}
