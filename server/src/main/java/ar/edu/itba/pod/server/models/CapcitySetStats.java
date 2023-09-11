package ar.edu.itba.pod.server.models;

public record CapcitySetStats(
        Integer confirmed,
        Integer moved,
        Integer cancelled
) {}
