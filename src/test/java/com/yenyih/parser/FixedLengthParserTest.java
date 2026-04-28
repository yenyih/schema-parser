package com.yenyih.parser;

import com.yenyih.model.ColumnInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FixedLengthParserTest {

    private static final List<ColumnInfo> SCHEMA = List.of(
        new ColumnInfo("name", 1, 20),
        new ColumnInfo("gender", 20, 21),
        new ColumnInfo("age", 22, 25)
    );

    // Use a no-arg constructor for the parser
    private final FixedLengthParser parser = new FixedLengthParser();

    @TempDir
    Path tempDir;

    // Produces a 25-char fixed-width line matching the schema above.
    private static String line(String name, char gender, String age) {
        return String.format("%-19s %c %-3s", name, gender, age);
    }

    @Test
    void parsesValidSingleLine() throws IOException {
        Path file = tempDir.resolve("data.txt");
        Files.writeString(file, line("John Doe", 'M', "25"));

        // Pass the target class and schema to parseFile
        List<TestRecord> records = parser.parseFile(file.toString(), TestRecord.class, SCHEMA);

        assertThat(records).hasSize(1);
        assertThat(records.get(0).toString())
            .isEqualTo("TestRecord{name='John Doe', gender='M', age='25'}");
    }

    @Test
    void parsesMultipleLines() throws IOException {
        Path file = tempDir.resolve("data.txt");
        Files.writeString(file,
            line("John Doe", 'M', "25") + "\n" + line("Jane Smith", 'F', "30"));

        List<TestRecord> records = parser.parseFile(file.toString(), TestRecord.class, SCHEMA);

        assertThat(records).hasSize(2);
        assertThat(records.get(0).toString())
            .isEqualTo("TestRecord{name='John Doe', gender='M', age='25'}");
        assertThat(records.get(1).toString())
            .isEqualTo("TestRecord{name='Jane Smith', gender='F', age='30'}");
    }

    @Test
    void skipsShortLines() throws IOException {
        Path file = tempDir.resolve("data.txt");
        Files.writeString(file,
            line("John Doe", 'M', "25") + "\nshort\n" + line("Jane Smith", 'F', "30"));

        List<TestRecord> records = parser.parseFile(file.toString(), TestRecord.class, SCHEMA);

        assertThat(records).hasSize(2);
    }

    @Test
    void returnsEmptyListForEmptyFile() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        List<TestRecord> records = parser.parseFile(file.toString(), TestRecord.class, SCHEMA);

        assertThat(records).isEmpty();
    }

    @Test
    void fieldsAreTrimmed() throws IOException {
        Path file = tempDir.resolve("data.txt");
        Files.writeString(file, line("John Doe", 'M', "25"));

        List<TestRecord> records = parser.parseFile(file.toString(), TestRecord.class, SCHEMA);

        assertThat(records.get(0).toString()).doesNotContain("  ");
    }

    /**
     * A simple POJO to act as the target class for parsing.
     * It must have a no-arg constructor and setters matching the schema fields.
     */
    public static class TestRecord {
        private String name;
        private String gender;
        private String age;

        public TestRecord() {}

        public void setName(String name) {
            this.name = name;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public void setAge(String age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "TestRecord{name='" + name + "', gender='" + gender + "', age='" + age + "'}";
        }
    }
}
