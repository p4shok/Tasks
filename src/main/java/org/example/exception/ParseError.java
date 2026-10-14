package org.example.exception;

public record ParseError(int lineNumber, String rawLine, String reason) {

    @Override
    public String toString() {
        return rawLine + " - " + reason;
    }

    public String describe() {
        return "Строка " + lineNumber + ": " + reason + " -> \"" + rawLine + "\"";
    }
}
