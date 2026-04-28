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
            "public class Record {\n" +
            "    private String name;\n" +
            "\n" +
            "    public Record() {\n" +
            "    }\n\n" +
            "    public Record(String name) {\n" +
            "        this.name = name;\n" +
            "    }\n\n" +
            "    public String getName() {\n" +
            "        return name;\n" +
            "    }\n\n" +
            "    public void setName(String name) {\n" +
            "        this.name = name;\n" +
            "    }\n\n" +
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
            "public class Record {\n" +
            "    private String name;\n" +
            "    private String age;\n" +
            "\n" +
            "    public Record() {\n" +
            "    }\n\n" +
            "    public Record(String name, String age) {\n" +
            "        this.name = name;\n" +
            "        this.age = age;\n" +
            "    }\n\n" +
            "    public String getName() {\n" +
            "        return name;\n" +
            "    }\n\n" +
            "    public void setName(String name) {\n" +
            "        this.name = name;\n" +
            "    }\n\n" +
            "    public String getAge() {\n" +
            "        return age;\n" +
            "    }\n\n" +
            "    public void setAge(String age) {\n" +
            "        this.age = age;\n" +
            "    }\n\n" +
            "    @Override\n" +
            "    public String toString() {\n" +
            "        return \"Record{name='\" + name + \"', age='\" + age + \"'}\";\n" +
            "    }\n" +
            "}",
            result);
    }

    @Test
    void generatesEmptyClass() {
        String result = generator.generate(List.of());

        assertEquals(
            "public class Record {\n" +
            "\n" +
            "    public Record() {\n" +
            "    }\n\n" +
            "    @Override\n" +
            "    public String toString() {\n" +
            "        return \"Record{}\";\n" +
            "    }\n" +
            "}",
            result);
    }
}
