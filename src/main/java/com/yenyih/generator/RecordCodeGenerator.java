package com.yenyih.generator;

import com.yenyih.model.ColumnInfo;

import java.util.List;
import java.util.stream.Collectors;

public class RecordCodeGenerator {

    public String generate(List<ColumnInfo> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("public class Record {\n");

        // Private fields
        for (ColumnInfo col : columns) {
            sb.append("    private String ").append(col.name()).append(";\n");
        }
        sb.append("\n");

        // No-args constructor
        sb.append("    public Record() {\n");
        sb.append("    }\n\n");

        // All-args constructor
        if (!columns.isEmpty()) {
            sb.append("    public Record(");
            String params = columns.stream()
                .map(col -> "String " + col.name())
                .collect(Collectors.joining(", "));
            sb.append(params).append(") {\n");
            for (ColumnInfo col : columns) {
                sb.append("        this.").append(col.name()).append(" = ").append(col.name()).append(";\n");
            }
            sb.append("    }\n\n");
        }

        // Getters and Setters
        for (ColumnInfo col : columns) {
            String fieldName = col.name();
            String capitalizedField = capitalize(fieldName);

            // Getter
            sb.append("    public String get").append(capitalizedField).append("() {\n");
            sb.append("        return ").append(fieldName).append(";\n");
            sb.append("    }\n\n");

            // Setter
            sb.append("    public void set").append(capitalizedField).append("(String ").append(fieldName).append(") {\n");
            sb.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
            sb.append("    }\n\n");
        }

        // toString method
        sb.append("    @Override\n");
        sb.append("    public String toString() {\n");
        sb.append("        return ").append(buildToStringReturn(columns)).append(";\n");
        sb.append("    }\n");

        sb.append("}");
        return sb.toString();
    }

    private String buildToStringReturn(List<ColumnInfo> columns) {
        if (columns.isEmpty()) {
            return "\"Record{}\"";
        }
        StringBuilder sb = new StringBuilder("\"Record{");
        for (int i = 0; i < columns.size(); i++) {
            String name = columns.get(i).name();
            sb.append(name).append("='\" + ").append(name).append(" + \"'");
            if (i < columns.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("}\"");
        return sb.toString();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
