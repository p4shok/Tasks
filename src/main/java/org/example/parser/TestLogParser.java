package org.example.parser;

import org.example.exception.BrokenLineException;
import org.example.exception.ParseError;
import org.example.model.*;

import java.util.*;

public class TestLogParser {

    private static final String DELIMITER_REGEX = "\\|";
    private static final int EXPECTED_FIELDS = 3;

    public ParseReport parse(List<String> lines) {
        Map<String, TestResult> lastByTest = new LinkedHashMap<>();
        List<ParseError> errors = new ArrayList<>();

        int lineNumber = 0;
        for(String line : lines) {
            lineNumber++;

            try {
                TestResult result = parseLineOrThrow(line);
                lastByTest.put(result.name(), result);
            } catch (BrokenLineException e) {
                errors.add(new ParseError(lineNumber, line, e.getMessage()));
            }
        }

        return new ParseReport(new ArrayList<>(lastByTest.values()), errors);
    }

    public static Optional<TestResult> firstFailed(List<TestResult> results) {
        return results.stream()
                .filter(r -> r.log() == Status.FAILED)
                .findFirst();
    }

    private TestResult parseLineOrThrow(String line) {
        if(line == null || line.isBlank()) {
            throw new BrokenLineException("Пустая строка");
        }

        String[] parts = line.split(DELIMITER_REGEX, -1);
        if(parts.length != EXPECTED_FIELDS) {
            throw new BrokenLineException("Ожидалось " + EXPECTED_FIELDS + " поля, найдено: " + parts.length);
        }

        String name = parts[0].trim();
        String statusRaw = parts[1].trim();
        String timeRaw = parts[2].trim();

        if(name.isEmpty()) {
            throw new BrokenLineException("Пустое имя теста");
        }

        Status status;
        try {
            status = Status.valueOf(statusRaw);
        } catch (IllegalArgumentException e) {
            throw new BrokenLineException("Недопустимый статус: " + statusRaw);
        }


        long durationMs;
        try {
            durationMs = Long.parseLong(timeRaw);
        } catch (NumberFormatException e) {
            throw new BrokenLineException("Время не является числом: " + timeRaw);
        }

        if (durationMs < 0) {
            throw new BrokenLineException("Отрицательное время: " + durationMs);
        }

        return new TestResult(name, status, durationMs);
    }
}
