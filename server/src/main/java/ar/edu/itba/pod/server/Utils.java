package ar.edu.itba.pod.server;

public class Utils {
    public static String minutesToTime(Integer minutes) {
        if (minutes == null) {
            throw new IllegalArgumentException("Minutes cannot be null");
        }
        if (minutes < 0 || minutes > 1439) {
            throw new IllegalArgumentException("Minutes must be a number between 0 and 1439");
        }
        int hours = minutes / 60;
        int minutesLeft = minutes % 60;
        return String.format("%02d:%02d", hours, minutesLeft);
    }
}
