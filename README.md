# schema-parser

Schema-driven Java class generator and fixed-length data parser. Plain Java + Maven + Janino.

## Purpose

1.  **Generate**: Reads a user-editable schema file and generates a traditional Java source code string for a [Record](output/Record.java#L0-L42) class (fields, constructors, getters, setters, `toString`).
2.  **Compile**: Uses [Janino](https://janino-compiler.github.io/) to compile the generated source code in-memory at runtime.
3.  **Parse**: Reads a fixed-length data file, maps the data to the dynamically compiled [Record](output/Record.java#L0-L42) class using reflection, and outputs the records.

Re-run whenever the schema or data changes. No static `.java` files are written to disk for the record structure; it exists only in memory during execution.

## Schema File Format

One field per line, space-delimited:

```text
name   1  20
gender 20 21
age    22 25
```

| Token | Description |
|---|---|
| field name | Valid Java identifier for the column (no spaces, no reserved words) |
| start | 1-based inclusive start position |
| end | 1-based inclusive end position |

## Data File Format

A plain text file where each line corresponds to a record. Fields are extracted based on the start/end positions defined in the schema.

```text
John Doe            M 25
Jane Smith          F 30
```

## Architecture

-   **Plain Maven project**, Java 25
-   **[Main.java](src/main/java/com/yenyih/Main.java)** — Entry point. Orchestrates reading schema, generating code, compiling with Janino, and parsing data.
-   **[SchemaReader](src/main/java/com/yenyih/reader/SchemaReader.java)** — Parses [schema.txt](schema.txt) into `List<ColumnInfo>`.
-   **[RecordCodeGenerator](src/main/java/com/yenyih/generator/RecordCodeGenerator.java)** — Converts `List<ColumnInfo>` into a Java class source string.
-   **[FixedLengthParser](src/main/java/com/yenyih/parser/FixedLengthParser.java)** — Reads a data file and populates instances of a provided class using reflection and schema positions.

## Key Classes

| Class | Responsibility |
|---|---|
| [Main](src/main/java/com/yenyih/Main.java) | Entry point, wires pipeline, handles errors |
| `ColumnInfo` | Immutable record: field name, start position, end position |
| [SchemaReader](src/main/java/com/yenyih/reader/SchemaReader.java) | Reads and validates schema file, throws [SchemaParseException](src/main/java/com/yenyih/reader/SchemaParseException.java#L2-L10) on bad lines |
| [RecordCodeGenerator](src/main/java/com/yenyih/generator/RecordCodeGenerator.java) | Stateless: `List<ColumnInfo>` → `public class Record { ... }` string |
| [FixedLengthParser](src/main/java/com/yenyih/parser/FixedLengthParser.java) | Generic parser: takes `Class<T>` and `List<ColumnInfo>`, returns `List<T>` populated via reflection |
| [SchemaParseException](src/main/java/com/yenyih/reader/SchemaParseException.java#L2-L10) | Unchecked exception for malformed schema lines |

## Generated Code Structure

The generator produces a standard Java bean-like structure in memory:

```java
public class Record {
    private String name;
    private String gender;
    private String age;

    public Record() {}

    public Record(String name, String gender, String age) {
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // ... other getters and setters ...

    @Override
    public String toString() {
        return "Record{name='John Doe', gender='M', age='25'}";
    }
}
```

## Error Handling

| Scenario | Behaviour |
|---|---|
| [schema.txt](schema.txt) not found | Print error to stderr, exit code 1 |
| [valid-records.txt](valid-records.txt) not found | Print error to stderr, exit code 1 |
| Malformed schema line | Throw [SchemaParseException](src/main/java/com/yenyih/reader/SchemaParseException.java#L2-L10), print to stderr, exit code 1 |
| Invalid field name (keyword/identifier) | Throw [SchemaParseException](src/main/java/com/yenyih/reader/SchemaParseException.java#L2-L10), print to stderr, exit code 1 |
| Data line too short | Print warning to stderr, skip line |
| Reflection failure (missing setter) | Throw `RuntimeException`, print to stderr, exit code 1 |

## Run

1.  Ensure [schema.txt](schema.txt) and [valid-records.txt](valid-records.txt) exist in the project root.
2.  Compile and run:

```bash
mvn compile exec:java -Dexec.mainClass="com.yenyih.Main"
```

The application will print the generated source code (for debugging) and then the parsed records to stdout.

## Tests

```bash
mvn test
```

| Test Class | What it covers |
|---|---|
| [SchemaReaderTest](src/test/java/com/yenyih/reader/SchemaReaderTest.java#L14-L83) | Valid schema, blank lines, wrong column count, non-numeric positions, invalid identifiers |
| [RecordCodeGeneratorTest](src/test/java/com/yenyih/generator/RecordCodeGeneratorTest.java#L9-L94) | Single/multiple fields, empty schema, correct Java syntax generation |
| [FixedLengthParserTest](src/test/java/com/yenyih/parser/FixedLengthParserTest.java#L13-L119) | Parsing data into a generic POJO ([TestRecord](src/test/java/com/yenyih/parser/FixedLengthParserTest.java#L95-L118)), skipping short lines, trimming fields |
