package ar.edu.itba.pod.client.serializers.table;

public enum ColumnAlignment {
    Left,
    Right;

    public String getFormatSign() {
        return switch (this) {
            case Left -> "-";
            case Right -> "";
        };
    }
}

