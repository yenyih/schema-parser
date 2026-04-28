package com.yenyih.writer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CodeFileWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesFileToCorrectPath() throws IOException {
        var outputPath = tempDir.resolve("Record.java");

        new CodeFileWriter(outputPath).write("public record Record() {}");

        assertEquals("public record Record() {}", Files.readString(outputPath));
    }

    @Test
    void createsMissingOutputDirectory() {
        var outputPath = tempDir.resolve("output/Record.java");

        new CodeFileWriter(outputPath).write("public record Record() {}");

        assertTrue(Files.exists(outputPath));
    }
}
