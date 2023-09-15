package ar.edu.itba.pod.client.notification.actions;

import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import ar.edu.itba.pod.client.serializers.DayOfYearSerializer;
import ar.edu.itba.pod.client.serializers.TimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Park;

public class FollowAction extends NotificationClientAction {
    private static final Logger logger = LoggerFactory.getLogger(FollowAction.class);

    public FollowAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run(Clients clients) {
        var observer = clients.getNotificationService().registerUser(
                Park.NotificationRequest.newBuilder()
                        .setName(attraction)
                        .setDay(day)
                        .setUserId(visitor)
                        .build()
        );
        observer.forEachRemaining(response -> {
            System.out.println(response.getMessage());
        });
    }

    @Override
    public String toString() {
        return String.format("FollowAction %s", super.toString());
    }
}
