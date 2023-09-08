package ar.edu.itba.pod.client.serializers.table.specific;

import ar.edu.itba.pod.client.serializers.TimeSerializer;
import ar.edu.itba.pod.client.serializers.UUIDSerializer;
import ar.edu.itba.pod.client.serializers.table.ColumnAlignment;
import ar.edu.itba.pod.client.serializers.table.ColumnProperties;
import ar.edu.itba.pod.client.serializers.table.TableWriter;
import services.Park;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class CapacityQueryTableWriter extends TableWriter {
    static final String[] header = {"Slot", "Capacity", "Attraction"};

    public CapacityQueryTableWriter(OutputStreamWriter outputWriter) throws IOException {
        super(outputWriter, new ColumnProperties[]{
                new ColumnProperties(5, ColumnAlignment.Right),
                new ColumnProperties(header[1].length(), ColumnAlignment.Right),
                new ColumnProperties(1, ColumnAlignment.Left),
        });
        super.addRow(header);
    }

    /**
     * Adds row
     *
     * @param slot
     * @param capacity
     * @param attraction
     * @throws IOException
     */
    public void addRow(int slot, int capacity, String attraction) throws IOException {
        super.addRow(new String[]{
                new TimeSerializer().serialize(slot),
                Integer.toString(capacity),
                attraction
        });
    }
}
