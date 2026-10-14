package org.example.model;

public enum Status {
    PASSED,
    FAILED,
    SKIPPED;

    public static Status fromString(String raw) {
        if(raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("пустой статус");
        }

        try {
            return Status.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("неизвестный статус: " + raw);
        }
    }
}
