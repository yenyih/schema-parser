package com.yenyih.generator;

import com.yenyih.model.ColumnInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecordCodeGeneratorTest {

    private final RecordCodeGenerator generator = new RecordCodeGenerator();

    @Test
    void generatesSingleField() {
        String result = generator.generate(List.of(new ColumnInfo("name", 1, 20)));

        assertEquals(
            "public record Record(String name) {\n" +
            "    @Override\n" +
            "    public String toString() {\n" +
            "        return \"Record{name='\" + name + \"'}\";\n" +
            "    }\n" +
            "}",
            result);
    }

    @Test
    void generatesMultipleFields() {
        String result = generator.generate(List.of(
            new ColumnInfo("name", 1, 20),
            new ColumnInfo("age", 22, 25)
        ));

        assertEquals(
            "public record Record(String name, String age) {\n" +
            "    @Override\n" +
            "    public String toString() {\n" +
            "        return \"Record{name='\" + name + \"', age='\" + age + \"'}\";\n" +
            "    }\n" +
            "}",
            result);
    }

    @Test
    void generatesEmptyRecord() {
        String result = generator.generate(List.of());

        assertEquals(
            "public record Record() {\n" +
            "    @Override\n" +
            "    public String toString() {\n" +
            "        return \"Record{}\";\n" +
            "    }\n" +
            "}",
            result);
    }
}
