package ar.edu.itba.pod.client.serializers;

public interface Serializer<T> {
    String serialize(T value);
}
