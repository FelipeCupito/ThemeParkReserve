package ar.edu.itba.pod.client.serializers.table.specific;

import ar.edu.itba.pod.client.serializers.TimeSerializer;
import ar.edu.itba.pod.client.serializers.table.ColumnAlignment;
import ar.edu.itba.pod.client.serializers.table.ColumnProperties;
import ar.edu.itba.pod.client.serializers.table.TableWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class AttractionsTableWriter extends TableWriter {
    static final String[] header = {"Open", "Close", "Name"};

    public AttractionsTableWriter(OutputStreamWriter outputWriter) throws IOException {
        super(outputWriter, new ColumnProperties[]{
                new ColumnProperties(header[0].length() + 1, ColumnAlignment.Right),
                new ColumnProperties(header[1].length(), ColumnAlignment.Right),
                new ColumnProperties(1, ColumnAlignment.Left),
        });
        super.addRow(header);
    }

    /**
     * Adds row
     */
    public void addRow(int open, int close, String attraction) throws IOException {
        super.addRow(new String[]{
                new TimeSerializer().serialize(open),
                new TimeSerializer().serialize(close),
                attraction
        });
    }
}
