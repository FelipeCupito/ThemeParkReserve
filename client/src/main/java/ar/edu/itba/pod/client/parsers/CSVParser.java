package ar.edu.itba.pod.client.parsers;

import ar.edu.itba.pod.client.parsers.exceptions.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class CSVParser<T, P extends CSVLineParser<T>> {
    Path path;
    P lineParser;
    boolean hasHeader;

    public CSVParser(Path path, P lineParser, boolean hasHeader) {
        this.path = path;
        this.lineParser = lineParser;
        this.hasHeader = hasHeader;
    }

    public Stream<T> toStream() throws IOException {
        var lines = Files.lines(path);
        if (hasHeader) {
            lines = lines.skip(1);
        }
        return lines
                .map((line) -> {
                    try {
                        return lineParser.parse(line);
                    } catch (ParseException e) {
                        throw new RuntimeException(String.format("Incorrect file format: %s", e));
                    }
                });
    }
}
