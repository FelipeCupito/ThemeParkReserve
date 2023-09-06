package ar.edu.itba.pod.server.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Park {

    private final Map<String, Attraction> attractions = new HashMap<>();
    private final Map<UUID, Pass> passes = new HashMap<>();



}
