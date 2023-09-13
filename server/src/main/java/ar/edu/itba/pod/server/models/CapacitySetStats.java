package ar.edu.itba.pod.server.models;

public record CapacitySetStats(
        Integer confirmed,
        Integer moved,
        Integer cancelled
) {}
