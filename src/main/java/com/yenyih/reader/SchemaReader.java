package com.yenyih.reader;

import com.yenyih.model.ColumnInfo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SchemaReader {

    private static final Set<String> JAVA_KEYWORDS = Set.of(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
        "class", "const", "continue", "default", "do", "double", "else", "enum",
        "extends", "final", "finally", "float", "for", "goto", "if", "implements",
        "import", "instanceof", "int", "interface", "long", "native", "new",
        "package", "private", "protected", "public", "return", "short", "static",
        "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "var", "void", "volatile", "while", "record", "sealed",
        "permits", "yield", "when"
    );

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
        var fieldName = parts[0];
        if (!isValidJavaIdentifier(fieldName)) {
            throw new SchemaParseException(
                "Line " + lineNumber + " has invalid Java identifier \"" + fieldName + "\""
            );
        }
        try {
            return new ColumnInfo(fieldName, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            throw new SchemaParseException(
                "Line " + lineNumber + " has non-numeric start/end: \"" + line + "\"", e
            );
        }
    }

    private static boolean isValidJavaIdentifier(String name) {
        if (name.isEmpty()) return false;
        if (!Character.isJavaIdentifierStart(name.charAt(0))) return false;
        for (int i = 1; i < name.length(); i++) {
            if (!Character.isJavaIdentifierPart(name.charAt(i))) return false;
        }
        return !JAVA_KEYWORDS.contains(name);
    }
}
