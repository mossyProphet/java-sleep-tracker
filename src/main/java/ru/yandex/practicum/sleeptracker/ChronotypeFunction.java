package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypeFunction implements Function {

    @Override
    public SleepAnalysisResult<?> apply(List<SleepingSession> sessions) {
        Map<Chronotype, Long> counts = sessions.stream()
                .filter(this::isNightSession)
                .map(this::classify)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        long owl = counts.getOrDefault(Chronotype.OWL, 0L);
        long lark = counts.getOrDefault(Chronotype.LARK, 0L);
        long pigeon = counts.getOrDefault(Chronotype.PIGEON, 0L);

        Chronotype result = resolve(owl, lark, pigeon);
        return new SleepAnalysisResult<>("Хронотип пользователя", result);
    }

    private Chronotype resolve(long owl, long lark, long pigeon) {
        long max = Math.max(owl, Math.max(lark, pigeon));
        // При равенстве максимумов — голубь
        long winners = (owl == max ? 1 : 0) + (lark == max ? 1 : 0) + (pigeon == max ? 1 : 0);
        if (winners > 1 || max == 0) return Chronotype.PIGEON;
        if (owl == max) return Chronotype.OWL;
        if (lark == max) return Chronotype.LARK;
        return Chronotype.PIGEON;
    }

    /** Игнорируем дневные сессии и сессии, не пересекающие ночь. */
    private boolean isNightSession(SleepingSession s) {
        LocalTime start = s.getSleepStart().toLocalTime();
        LocalTime end = s.getSleepEnd().toLocalTime();
        boolean crossesMidnight = s.getSleepEnd().toLocalDate()
                .isAfter(s.getSleepStart().toLocalDate());
        // Ночная — если пересекает [00:00, 06:00]
        if (crossesMidnight) return true;
        // Иначе должна попадать в интервал до 6 утра
        return start.isBefore(LocalTime.of(6, 0)) && end.isAfter(LocalTime.MIDNIGHT);
    }

    private Chronotype classify(SleepingSession s) {
        LocalTime start = s.getSleepStart().toLocalTime();
        LocalTime end = s.getSleepEnd().toLocalTime();

        boolean owl = start.isAfter(LocalTime.of(23, 0)) && end.isAfter(LocalTime.of(9, 0));
        boolean lark = start.isBefore(LocalTime.of(22, 0)) && end.isBefore(LocalTime.of(7, 0));

        if (owl) return Chronotype.OWL;
        if (lark) return Chronotype.LARK;
        return Chronotype.PIGEON;
    }
}
