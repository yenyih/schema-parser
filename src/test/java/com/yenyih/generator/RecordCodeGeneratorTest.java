package com.yenyih.generator;

import com.yenyih.model.ColumnInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecordCodeGeneratorTest {

    private final RecordCodeGenerator generator = new RecordCodeGenerator();

    @Test
    void generatesSingleField() {
        var result = generator.generate(List.of(new ColumnInfo("name", 1, 20)));

        assertEquals("public record Record(String name) {}", result);
    }

    @Test
    void generatesMultipleFields() {
        var result = generator.generate(List.of(
            new ColumnInfo("name", 1, 20),
            new ColumnInfo("age", 22, 25)
        ));

        assertEquals("public record Record(String name, String age) {}", result);
    }

    @Test
    void generatesEmptyRecord() {
        var result = generator.generate(List.of());

        assertEquals("public record Record() {}", result);
    }
}
