package ar.edu.itba.pod.client.serializers.table;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TableWriter implements Closeable {
    ColumnProperties[] columnProperties;
    OutputStreamWriter outputWriter;

    public TableWriter(OutputStreamWriter outputWriter, ColumnProperties[] columnProperties) {
        this.columnProperties = columnProperties;
        this.outputWriter = outputWriter;
    }

    public void addRow(String[] values) throws IOException {
        if (values.length != columnProperties.length)
            throw new IllegalArgumentException("All rows should have the same amount of columns");

        for (int column = 0; column < values.length; column++) {
            var columnProperties = this.columnProperties[column];
            var value = values[column];
            // Not first column
            if (column > 0)
                outputWriter.append('|');
            outputWriter.append(String.format(columnProperties.getFormatString(), value));
        }
        outputWriter.append('\n');
    }

    @Override
    public void close() throws IOException {
        outputWriter.close();
    }
}
