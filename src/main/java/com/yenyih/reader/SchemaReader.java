package com.yenyih.reader;

import com.yenyih.model.ColumnInfo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SchemaReader {

    private final Path schemaPath;

    public SchemaReader(Path schemaPath) {
        this.schemaPath = schemaPath;
    }

    public List<ColumnInfo> read() {
        // Schema files are always small; readAllLines is fine here
        List<String> lines;
        try {
            lines = Files.readAllLines(schemaPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        var result = new ArrayList<ColumnInfo>();
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (!line.isBlank()) {
                result.add(parseLine(i + 1, line));
            }
        }
        return result;
    }

    private ColumnInfo parseLine(int lineNumber, String line) {
        var parts = line.trim().split("\\s+"); // trim handles accidental leading/trailing whitespace in field names
        if (parts.length != 3) {
            throw new SchemaParseException(
                "Line " + lineNumber + " must have 3 columns, got " + parts.length + ": \"" + line + "\""
            );
        }
        try {
            return new ColumnInfo(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            throw new SchemaParseException(
                "Line " + lineNumber + " has non-numeric start/end: \"" + line + "\"", e
            );
        }
    }
}
