package com.yenyih.reader;

import com.yenyih.model.ColumnInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchemaReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesValidSchema() throws IOException {
        var schema = tempDir.resolve("schema.txt");
        Files.writeString(schema, "name 1 20\ngender 20 21\nage 22 25");

        var result = new SchemaReader(schema).read();

        assertEquals(List.of(
            new ColumnInfo("name", 1, 20),
            new ColumnInfo("gender", 20, 21),
            new ColumnInfo("age", 22, 25)
        ), result);
    }

    @Test
    void skipsBlankLines() throws IOException {
        var schema = tempDir.resolve("schema.txt");
        Files.writeString(schema, "name 1 20\n\nage 22 25\n");

        var result = new SchemaReader(schema).read();

        assertEquals(List.of(
            new ColumnInfo("name", 1, 20),
            new ColumnInfo("age", 22, 25)
        ), result);
    }

    @Test
    void throwsOnWrongColumnCount() throws IOException {
        var schema = tempDir.resolve("schema.txt");
        Files.writeString(schema, "name 1");

        assertThrows(SchemaParseException.class, () -> new SchemaReader(schema).read());
    }

    @Test
    void throwsOnNonNumericPositions() throws IOException {
        var schema = tempDir.resolve("schema.txt");
        Files.writeString(schema, "name abc 20");

        assertThrows(SchemaParseException.class, () -> new SchemaReader(schema).read());
    }

    @Test
    void throwsUncheckedIOExceptionForMissingFile() {
        var missing = tempDir.resolve("nonexistent.txt");
        assertThrows(UncheckedIOException.class, () -> new SchemaReader(missing).read());
    }
}
