package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NotificationRepository {
    Map<Park.UUID, ConcurrentLinkedQueue<Park.Slot>> notificationsMap = new HashMap<>();

    public boolean reservationExists(Park.UUID uuid, Park.Slot slot){
        return notificationsMap.containsKey(uuid) && notificationsMap.get(uuid).contains(slot);
    }

    public boolean addNotification(Park.UUID uuid, Park.Slot slot){
        if(!notificationsMap.containsKey(uuid)){
            notificationsMap.put(uuid, new ConcurrentLinkedQueue<>());
        }
        return notificationsMap.get(uuid).add(slot);
    }

}


