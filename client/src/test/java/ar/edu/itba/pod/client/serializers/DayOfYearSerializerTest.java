package ar.edu.itba.pod.client.serializers;

import org.junit.Test;

import static org.junit.Assert.*;

public class DayOfYearSerializerTest {

    @Test
    public void serialize() {
        var serializer = new DayOfYearSerializer();
        assertEquals("1", serializer.serialize(0));
        assertEquals("10", serializer.serialize(9));
    }
}