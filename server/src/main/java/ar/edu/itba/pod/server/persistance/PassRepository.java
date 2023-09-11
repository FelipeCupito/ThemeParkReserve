package ar.edu.itba.pod.server.persistance;

import services.Park;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PassRepository {
    private final ConcurrentMap<Park.UUID, ConcurrentMap<Integer, Park.PassType>> passes = new ConcurrentHashMap<>();

    public void addPass(Park.UUID userId, Integer day, Park.PassType passType) {
        if (userId == null) {
            throw new IllegalArgumentException("Park id cannot be null");
        }
        if (passType == null) {
            throw new IllegalArgumentException("Pass type cannot be null");
        }
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between a and 365");
        }

        if (passType != Park.PassType.PASS_UNLIMITED && passType != Park.PassType.PASS_HALF_DAY && passType != Park.PassType.PASS_THREE) {
            throw new IllegalArgumentException("Pass type must be valid");
        }

        passes.putIfAbsent(userId, new ConcurrentHashMap<>());

        if (passes.get(userId).containsKey(day)) {
            throw new IllegalArgumentException("Pass already exists");
        }
        passes.get(userId).put(day, passType);
    }

    public boolean passExists(Park.UUID userId, Integer day) {
        if (userId == null) {
            return false;
        }
        if (day == null || day <= 1 || day > 365) {
            return false;
        }
        return passes.containsKey(userId) && passes.get(userId).containsKey(day);
    }

    public Park.PassType getPassType(Park.UUID userId, Integer day) {
        if (userId == null) {
            throw new IllegalArgumentException("Park id cannot be null");
        }
        if (day == null || day <= 1 || day > 365) {
            throw new IllegalArgumentException("Day must be a number between a and 365");
        }
        if (!passes.containsKey(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        if (!passes.get(userId).containsKey(day)) {
            throw new IllegalArgumentException("Pass does not exist");
        }
        return passes.get(userId).get(day);
    }
}
