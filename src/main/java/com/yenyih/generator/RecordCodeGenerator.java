package com.yenyih.generator;

import com.yenyih.model.ColumnInfo;

import java.util.List;
import java.util.stream.Collectors;

public class RecordCodeGenerator {

    public String generate(List<ColumnInfo> columns) {
        var params = columns.stream()
            .map(col -> "String " + col.name())
            .collect(Collectors.joining(", "));
        var toStringReturn = buildToStringReturn(columns);
        return "public record Record(" + params + ") {\n" +
               "    @Override\n" +
               "    public String toString() {\n" +
               "        return " + toStringReturn + ";\n" +
               "    }\n" +
               "}";
    }

    private String buildToStringReturn(List<ColumnInfo> columns) {
        if (columns.isEmpty()) {
            return "\"Record{}\"";
        }
        var sb = new StringBuilder("\"Record{");
        for (int i = 0; i < columns.size(); i++) {
            var name = columns.get(i).name();
            sb.append(name).append("='\" + ").append(name).append(" + \"'");
            if (i < columns.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("}\"");
        return sb.toString();
    }
}
