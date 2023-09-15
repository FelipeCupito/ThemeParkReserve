package ar.edu.itba.pod.client.serializers.table;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TableWriter implements Closeable {
    private ColumnProperties[] columnProperties;
    private OutputStreamWriter outputWriter;
    private ColumnAlignment headerAlignment;
    private int currentRow = 0;

    public TableWriter(OutputStreamWriter outputWriter, ColumnAlignment headerAlignment, ColumnProperties[] columnProperties) {
        this.columnProperties = columnProperties;
        this.outputWriter = outputWriter;
        this.headerAlignment = headerAlignment;
    }

    public TableWriter(OutputStreamWriter outputWriter, ColumnProperties[] columnProperties) {
        this(outputWriter, ColumnAlignment.Left, columnProperties);
    }

    public void addRow(String[] values) throws IOException {
        if (values.length != columnProperties.length)
            throw new IllegalArgumentException("All rows should have the same amount of columns");

        var firstRow = currentRow == 0;
        for (int column = 0; column < values.length; column++) {
            var firstColumn = column == 0;
            var lastColumn = column == values.length - 1;
            var columnProperties = firstRow
                    ? new ColumnProperties(this.columnProperties[column].width, headerAlignment)
                    : this.columnProperties[column];
            var value = values[column];
            // Not first column
            if (!firstColumn)
                outputWriter.append('|');

            var valueBuilder = new StringBuilder();
            if (!firstColumn)
                valueBuilder.append(' ');
            valueBuilder.append(String.format(columnProperties.getFormatString(), value));
            if (!lastColumn)
                valueBuilder.append(' ');
            var formattedValue = valueBuilder.toString();
            if (lastColumn)
                formattedValue = formattedValue.stripTrailing();
            outputWriter.append(formattedValue);
        }
        outputWriter.append('\n');

        currentRow++;
    }

    @Override
    public void close() throws IOException {
        outputWriter.close();
    }
}
