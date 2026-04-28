package com.yenyih;

import com.yenyih.generator.RecordCodeGenerator;
import com.yenyih.model.ColumnInfo;
import com.yenyih.reader.SchemaParseException;
import com.yenyih.reader.SchemaReader;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import org.codehaus.janino.SimpleCompiler;

public class Main {
    public static void main(String[] args) {
        Path schemaPath = Path.of("schema.txt");

        try {
            List<ColumnInfo> columns = new SchemaReader(schemaPath).read();
            String source = new RecordCodeGenerator().generate(columns);
            System.out.println(source);
            System.out.println("Generated: " + source);

            SimpleCompiler compiler = new SimpleCompiler();
            compiler.cook(source); // Compiles the source
            Class<?> clazz = compiler.getClassLoader().loadClass("Record");
            Object instance = clazz.getDeclaredConstructor().newInstance();
        } catch (SchemaParseException | UncheckedIOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }
}
