package com.yenyih.parser;

import com.yenyih.model.ColumnInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FixedLengthParser {

    /**
     * Parses the file and returns a list of instances of the provided class.
     *
     * @param filePath Path to the data file
     * @param clazz    The compiled class to instantiate (e.g., Record)
     * @param columns   The list of column definitions (needed for start/end positions)
     * @return List of populated objects
     */
    public <T> List<T> parseFile(String filePath, Class<T> clazz, List<ColumnInfo> columns) throws IOException {
        List<T> records = new ArrayList<>();

        // Calculate min length required for this specific column
        int minLength = columns.stream().mapToInt(ColumnInfo::end).max().orElse(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.length() < minLength) {
                    System.err.printf("Warning: line %d too short (%d chars), skipping%n",
                        lineNumber, line.length());
                    continue;
                }

                T instance = createInstance(clazz);
                populateInstance(instance, line, columns);
                records.add(instance);
            }
        }
        return records;
    }

    private <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }
    }

    private void populateInstance(Object instance, String line, List<ColumnInfo> columns) {
        for (ColumnInfo col : columns) {
            String value = extractField(line, col.start(), col.end());
            setField(instance, col.name(), value);
        }
    }

    private void setField(Object instance, String fieldName, String value) {
        try {
            // Find the setter method: setFieldName(String)
            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method setter = instance.getClass().getMethod(setterName, String.class);
            setter.invoke(instance, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }

    private String extractField(String line, int start, int end) {
        return line.substring(start - 1, Math.min(end, line.length())).trim();
    }
}
