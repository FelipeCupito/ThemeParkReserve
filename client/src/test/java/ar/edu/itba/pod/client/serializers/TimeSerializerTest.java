package ar.edu.itba.pod.client.serializers;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeSerializerTest {
    @Test
    public void serialize() {
        var serializer = new TimeSerializer();
        assertEquals("00:00", serializer.serialize(0));
        assertEquals("00:30", serializer.serialize(30));
        assertEquals("01:30", serializer.serialize(90));
        assertEquals("10:00", serializer.serialize(600));
        assertEquals("23:59", serializer.serialize(1439));
    }
}