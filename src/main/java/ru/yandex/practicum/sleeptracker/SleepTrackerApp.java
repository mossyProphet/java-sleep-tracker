package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SleepTrackerApp {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private final List<Function> functions = List.of(
            new TotalSessionsFunction(),
            new MinSleepLength(),
            new MaxSleepLength(),
            new AvSleepLength(),
            new BadNightsCount(),
            new SleeplessNights(),
            new ChronotypeFunction()
    );

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Укажите путь к файлу с логом сна");
            return;
        }

        try {
            List<SleepingSession> sessions = loadSessions(Path.of(args[0]));
            SleepTrackerApp app = new SleepTrackerApp();
            app.functions.stream()
                    .map(f -> f.apply(sessions))
                    .forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    public static List<SleepingSession> loadSessions(Path path) throws IOException {
        return Files.lines(path)
                .filter(line -> !line.isBlank())
                .map(SleepTrackerApp::parseLine)
                .collect(Collectors.toList());
    }

    private static SleepingSession parseLine(String line) {
        String[] parts = line.split(";");
        LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());
        return new SleepingSession(start, end, quality);
    }
}
