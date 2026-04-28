package com.yenyih;

import com.yenyih.generator.RecordCodeGenerator;
import com.yenyih.reader.SchemaParseException;
import com.yenyih.reader.SchemaReader;
import com.yenyih.writer.CodeFileWriter;

import java.io.UncheckedIOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        var schemaPath = Path.of("schema.txt");
        var outputPath = Path.of("output/Record.java");

        try {
            var columns = new SchemaReader(schemaPath).read();
            var source = new RecordCodeGenerator().generate(columns);
            new CodeFileWriter(outputPath).write(source);
            System.out.println("Generated: " + outputPath);
        } catch (SchemaParseException | UncheckedIOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
