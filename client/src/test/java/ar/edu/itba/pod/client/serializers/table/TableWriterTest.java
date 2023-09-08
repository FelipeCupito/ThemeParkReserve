package ar.edu.itba.pod.client.serializers.table;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static org.junit.Assert.assertEquals;

public class TableWriterTest {
    @Test
    public void simpleTable() throws IOException {
        var output = new ByteArrayOutputStream();
        var writer = new TableWriter(new OutputStreamWriter(output), new ColumnProperties[]{new ColumnProperties(5, ColumnAlignment.Left)});

        writer.addRow(new String[]{"Time"});
        writer.addRow(new String[]{"00:00"});
        writer.addRow(new String[]{"10:30"});
        writer.close();

        assertEquals("Time \n00:00\n10:30\n", output.toString());
    }

    @Test
    public void complexTable() throws IOException {
        var output = new ByteArrayOutputStream();
        var writer = new TableWriter(new OutputStreamWriter(output), new ColumnProperties[]{
                new ColumnProperties(6, ColumnAlignment.Right),
                new ColumnProperties(6, ColumnAlignment.Left),
                new ColumnProperties(1, ColumnAlignment.Left)
        });

        writer.addRow(new String[]{"Time", "Amount", "Name"});
        writer.addRow(new String[]{"00:00", "10", "Test 1"});
        writer.addRow(new String[]{"10:30", "6", "Very large text, this is a text"});
        writer.close();

        assertEquals("  Time|Amount|Name\n 00:00|10    |Test 1\n 10:30|6     |Very large text, this is a text\n", output.toString());
    }
}