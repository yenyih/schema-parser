package com.yenyih.generator;

import com.yenyih.model.ColumnInfo;

import java.util.List;
import java.util.stream.Collectors;

public class RecordCodeGenerator {

    public String generate(List<ColumnInfo> columns) {
        var params = columns.stream()
            .map(col -> "String " + col.name())
            .collect(Collectors.joining(", "));
        return "public record Record(" + params + ") {}";
    }
}
