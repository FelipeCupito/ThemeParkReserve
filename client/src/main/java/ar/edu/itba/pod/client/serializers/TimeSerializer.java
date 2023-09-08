package ar.edu.itba.pod.client.serializers;

public class TimeSerializer implements Serializer<Integer> {
    @Override
    public String serialize(Integer value) {
        return String.format("%02d:%02d", value / 60, value % 60);
    }
}
