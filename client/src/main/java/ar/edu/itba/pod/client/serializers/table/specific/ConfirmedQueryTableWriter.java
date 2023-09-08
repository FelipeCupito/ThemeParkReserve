package ar.edu.itba.pod.client.serializers.table.specific;

import ar.edu.itba.pod.client.serializers.TimeSerializer;
import ar.edu.itba.pod.client.serializers.UUIDSerializer;
import ar.edu.itba.pod.client.serializers.table.ColumnAlignment;
import ar.edu.itba.pod.client.serializers.table.ColumnProperties;
import ar.edu.itba.pod.client.serializers.table.TableWriter;
import services.Park;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class ConfirmedQueryTableWriter extends TableWriter {
    static final String[] header = {"Slot", "Visitor", "Attraction"};

    public ConfirmedQueryTableWriter(OutputStreamWriter outputWriter) throws IOException {
        super(outputWriter, new ColumnProperties[]{
                new ColumnProperties(5, ColumnAlignment.Right),
                new ColumnProperties(16*2+4, ColumnAlignment.Right),
                new ColumnProperties(0, ColumnAlignment.Left),
        });
        super.addRow(header);
    }

    /**
     * Adds row
     * @param slot
     * @param visitor
     * @param attraction
     * @throws IOException
     */
    public void addRow(int slot, Park.UUID visitor, String attraction) throws IOException {
        super.addRow(new String[]{
                new TimeSerializer().serialize(slot),
                new UUIDSerializer().serialize(visitor.getValue()),
                attraction
        });
    }
}
