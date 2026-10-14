package org.example.parser;

import org.example.exception.ParseError;
import org.example.model.TestResult;

import java.util.List;

public record ParseReport(List<TestResult> results, List<ParseError> errors) {

    public ParseReport {
        results = List.copyOf(results);
        errors = List.copyOf(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
