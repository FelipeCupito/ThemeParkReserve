package ar.edu.itba.pod.client.serializers.table.specific;

import ar.edu.itba.pod.client.serializers.TimeSerializer;
import ar.edu.itba.pod.client.serializers.table.ColumnAlignment;
import ar.edu.itba.pod.client.serializers.table.ColumnProperties;
import ar.edu.itba.pod.client.serializers.table.TableWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class AvailabilityTableWriter extends TableWriter {
    static final String[] header = {"Slot ", "Capacity ", "Pending  ", "Confirmed", "Attraction"};

    public AvailabilityTableWriter(OutputStreamWriter outputWriter) throws IOException {
        super(outputWriter, new ColumnProperties[]{
                new ColumnProperties(header[0].length(), ColumnAlignment.Right),
                new ColumnProperties(header[1].length(), ColumnAlignment.Right),
                new ColumnProperties(header[2].length(), ColumnAlignment.Right),
                new ColumnProperties(header[3].length(), ColumnAlignment.Right),
                new ColumnProperties(1, ColumnAlignment.Left),
        });
        super.addRow(header);
    }

    /**
     * Adds row
     * @param slot
     * @param capacity set to null to indicate capacity not yet loaded
     * @param pending
     * @param confirmed
     * @param attraction
     * @throws IOException
     */
    public void addRow(int slot, int capacity, int pending, int confirmed, String attraction) throws IOException {
        super.addRow(new String[]{
                new TimeSerializer().serialize(slot),
                capacity != 0 ? Integer.toString(capacity) : "X",
                Integer.toString(pending),
                Integer.toString(confirmed),
                attraction
        });
    }
}
