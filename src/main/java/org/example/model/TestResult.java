package org.example.model;

public record TestResult(String name, Status log, long durationMs) {
    public TestResult {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("имя теста пустое");
        }
        if(log == null) {
            throw new IllegalArgumentException("статус не задан");
        }
        if(durationMs < 0) {
            throw new IllegalArgumentException("Отрицательная длительность: " + durationMs);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof TestResult other)) return false;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
