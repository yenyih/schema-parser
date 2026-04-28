package com.yenyih;

import com.yenyih.generator.RecordCodeGenerator;
import com.yenyih.model.ColumnInfo;
import com.yenyih.parser.FixedLengthParser;
import com.yenyih.reader.SchemaParseException;
import com.yenyih.reader.SchemaReader;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import org.codehaus.janino.SimpleCompiler;

public class Main {
    public static void main(String[] args) {
        Path schemaPath = Path.of("schema.txt");
        Path dataPath = Path.of("valid-records.txt");

        try {
            // 1. Read Schema
            List<ColumnInfo> columns = new SchemaReader(schemaPath).read();

            // 2. Generate Code
            String source = new RecordCodeGenerator().generate(columns);

            // 3. Compile Code using Janino
            SimpleCompiler compiler = new SimpleCompiler();
            compiler.cook(source);
            Class<?> recordClass = compiler.getClassLoader().loadClass("Record");

            // 4. Parse Data
            FixedLengthParser parser = new FixedLengthParser();
            List<?> records = parser.parseFile(dataPath.toString(), recordClass, columns);

            // 5. Output Results
            records.forEach(System.out::println);
        } catch (SchemaParseException | UncheckedIOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }
}
