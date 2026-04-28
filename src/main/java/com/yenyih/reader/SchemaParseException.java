package com.yenyih.reader;

public class SchemaParseException extends RuntimeException {
    public SchemaParseException(String message) {
        super(message);
    }

    public SchemaParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
