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
        // TODO: Implement
        var slot = 600;
        var slotMoved = 630;
        var capacity = 630;
        var state = "PENDING"; // or "CONFIRMED" or "CANCELLED"
        clients.getNotificationService().registerUser(
                Park.NotificationRequest.newBuilder()
                        .setName(attraction)
                        .setDay(day)
                        .setUserId(visitor)
                        .build()
        );
        System.out.printf("The reservation for %s at %s on the day %s is %s\n",
                attraction,
                new TimeSerializer().serialize(slot),
                new DayOfYearSerializer().serialize(day),
                state
        );
        System.out.printf("The reservation for %s at %s on the day %s was moved to %s and is %s\n",
                attraction,
                new TimeSerializer().serialize(slot),
                new DayOfYearSerializer().serialize(day),
                new TimeSerializer().serialize(slotMoved),
                state
        );
        System.out.printf("%s announced slot capacity for the day %s: %d places.\n",
                attraction,
                new DayOfYearSerializer().serialize(day),
                capacity
        );
    }

    @Override
    public String toString() {
        return String.format("FollowAction %s", super.toString());
    }
}
