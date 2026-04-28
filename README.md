# schemaParser

Schema-driven Java record generator. Plain Java + Maven.

## Purpose

Reads a user-editable schema file and generates a Java 16+ `record` source file. Re-run whenever the schema changes.

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
- `Main.java` — entry point, reads `schema.txt` from working directory, writes to `output/Record.java`
- `SchemaReader` — parses schema file into `List<ColumnInfo>`
- `RecordCodeGenerator` — converts `List<ColumnInfo>` into a Java record source string
- `CodeFileWriter` — writes the generated source to disk, creating directories as needed

## Key Classes

| Class | Responsibility |
|---|---|
| `Main` | Entry point, wires pipeline, handles errors |
| `ColumnInfo` | Immutable record: field name, start position, end position |
| `SchemaReader` | Reads and validates schema file, throws `SchemaParseException` on bad lines |
| `RecordCodeGenerator` | Stateless: `List<ColumnInfo>` → `public record Record(...) {}` string |
| `CodeFileWriter` | Writes UTF-8 source to output path, creates parent directories |
| `SchemaParseException` | Unchecked exception for malformed schema lines |

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
