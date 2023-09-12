package ar.edu.itba.pod.client.serializers;

public class UUIDSerializer implements Serializer<String> {
    @Override
    public String serialize(String value) {
        return value.toLowerCase();
    }
}
