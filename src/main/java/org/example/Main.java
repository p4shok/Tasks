package org.example;

import org.example.model.TestResult;
import org.example.parser.ParseReport;
import org.example.parser.TestLogParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {

    private static final String LOG_FILE = "run.log";

    static void main(String[] args) throws IOException {
        List<String> lines = readLog(Path.of(LOG_FILE));

        TestLogParser parser = new TestLogParser();
        ParseReport report = parser.parse(lines);

        System.out.println("Результат");
        report.results().forEach(r ->
                System.out.printf(" %s — %s (%d ms)%n",
                        r.name(), r.log(), r.durationMs())
        );

        if(report.hasErrors()) {
            StringBuilder sb = new StringBuilder("Битые строки: ");
            for(int i = 0; i < report.errors().size(); i++) {
                if(i > 0) {
                    sb.append("; ");
                }
                sb.append(report.errors().get(i));
            }
            System.out.println(sb);
        } else {
            System.out.println("Битые строки: нет");
        }

        Optional<TestResult> failed = TestLogParser.firstFailed(report.results());
        if(failed.isPresent()) {
            TestResult r = failed.get();
            System.out.printf("Первый упавший: %s - %s (%d ms)%n", r.name(), r.log(), r.durationMs());
        } else {
            System.out.println("Первый упавший: падений нет");
        }
    }

    private static List<String> readLog(Path path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }
}