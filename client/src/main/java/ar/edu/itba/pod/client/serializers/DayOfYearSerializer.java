package ar.edu.itba.pod.client.serializers;

public class DayOfYearSerializer implements Serializer<Integer> {
    @Override
    public String serialize(Integer value) {
        return String.format("%d", value + 1);
    }
}
