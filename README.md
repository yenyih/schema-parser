# schema-parser

Schema-driven Java class generator. Plain Java + Maven.

## Purpose

Reads a user-editable schema file and generates a traditional Java source file containing a class with private fields, constructors, getters, setters, and a `toString` method. Re-run whenever the schema changes.

## Schema File Format

One field per line, space-delimited:

```
name   1  20
gender 20 21
age    22 25
```


| Token | Description |
|---|---|
| field name | Valid Java identifier for the column (no spaces, no reserved words) |
| start | 1-based inclusive start position |
| end | 1-based inclusive end position |

## Architecture

- Plain Maven project, Java 25
- [Main.java](src/main/java/com/yenyih/Main.java) — entry point, reads [schema.txt](schema.txt) from working directory, writes to [output/Record.java](output/Record.java)
- [SchemaReader](src/main/java/com/yenyih/reader/SchemaReader.java#L12-L79) — parses schema file into `List<ColumnInfo>`
- [RecordCodeGenerator](src/main/java/com/yenyih/generator/RecordCodeGenerator.java#L7-L84) — converts `List<ColumnInfo>` into a traditional Java class source string
- [CodeFileWriter](src/main/java/com/yenyih/writer/CodeFileWriter.java#L8-L27) — writes the generated source to disk, creating directories as needed

## Key Classes

| Class | Responsibility |
|---|---|
| [Main](src/main/java/com/yenyih/Main.java#L12-L27) | Entry point, wires pipeline, handles errors |
| `ColumnInfo` | Immutable record: field name, start position, end position |
| [SchemaReader](src/main/java/com/yenyih/reader/SchemaReader.java#L12-L79) | Reads and validates schema file, throws [SchemaParseException](src/main/java/com/yenyih/reader/SchemaParseException.java#L2-L10) on bad lines |
| [RecordCodeGenerator](src/main/java/com/yenyih/generator/RecordCodeGenerator.java#L7-L84) | Stateless: `List<ColumnInfo>` → `public class Record { ... }` string with fields, constructors, getters, setters |
| [CodeFileWriter](src/main/java/com/yenyih/writer/CodeFileWriter.java#L8-L27) | Writes UTF-8 source to output path, creates parent directories |
| [SchemaParseException](src/main/java/com/yenyih/reader/SchemaParseException.java#L2-L10) | Unchecked exception for malformed schema lines |

## Generated Code Structure

The generator produces a standard Java bean-like structure:

```java
public class Record {
    private String name;
    private String age;

    public Record() {
    }

    public Record(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ... other getters and setters ...

    @Override
    public String toString() {
        return "Record{name='" + name + "', age='" + age + "'}";
    }
}
```

## Error Handling

| Scenario | Behaviour |
|---|---|
| `schema.txt` not found | Print error to stderr, exit code 1 |
| Malformed schema line (wrong column count) | Throw `SchemaParseException` with line number, print to stderr, exit code 1 |
| Non-numeric start/end | Throw `SchemaParseException` with line number, print to stderr, exit code 1 |
| Field name is a Java reserved word or invalid identifier | Throw `SchemaParseException` with line number, print to stderr, exit code 1 |
| Output directory write failure | Print error to stderr, exit code 1 |

## Run

Edit `schema.txt` in the project root, then:

```bash
mvn compile exec:java -Dexec.mainClass="com.yenyih.Main"
```

Generated file is written to `output/Record.java`.

## Tests

```bash
mvn test
```

| Test Class | What it covers |
|---|---|
| `SchemaReaderTest` | Valid schema, blank lines skipped, wrong column count, non-numeric positions, Java keyword field names, invalid identifier field names, missing file |
| `RecordCodeGeneratorTest` | Single field, multiple fields, empty column list |
| `CodeFileWriterTest` | Writes to correct path, creates missing output directory |
