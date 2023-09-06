package ar.edu.itba.pod.server.models;

import java.util.*;

public class Park {
    private final Map<String, Attraction> attractions = new HashMap<>();
    private final Map<UUID, Pass> passes = new HashMap<>();
}