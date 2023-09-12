package ar.edu.itba.pod.client.serializers.table;

public class ColumnProperties {
    int width;
    ColumnAlignment alignment;

    public ColumnProperties(int width, ColumnAlignment alignment) {
        this.width = width;
        this.alignment = alignment;
    }

    public String getFormatString() {
        return '%' + alignment.getFormatSign() + width + 's';
    }
}
