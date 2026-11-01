package ru.yandex.practicum.sleeptracker;

import java.time.*;
import java.util.List;

public class SleeplessNights implements Function {

    @Override
    public SleepAnalysisResult<?> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
        }

        LocalDateTime firstStart = sessions.get(0).getSleepStart();
        LocalDateTime lastEnd = sessions.get(sessions.size() - 1).getSleepEnd();

        long totalNights = countTotalNights(firstStart, lastEnd);
        long sleptNights = sessions.stream()
                .filter(this::isNightSession)
                .map(this::nightDateOf)
                .distinct()
                .count();

        long sleepless = Math.max(0, totalNights - sleptNights);
        return new SleepAnalysisResult<>("Количество бессонных ночей", sleepless);
    }

    /** Сессия считается ночной, если пересекает [00:00, 06:00] какой-либо ночи. */
    private boolean isNightSession(SleepingSession s) {
        LocalDateTime start = s.getSleepStart();
        LocalDateTime end = s.getSleepEnd();
        // Ночь, к которой может относиться сессия
        LocalDate nightDate = nightDateOf(s);
        LocalDateTime nightStart = nightDate.atStartOfDay();
        LocalDateTime nightEnd = nightDate.atTime(6, 0);
        return start.isBefore(nightEnd) && end.isAfter(nightStart);
    }

    /** Определяем дату ночи, к которой относится сессия. */
    private LocalDate nightDateOf(SleepingSession s) {
        LocalDateTime start = s.getSleepStart();
        LocalDateTime end = s.getSleepEnd();
        // Если заснул до 06:00 — это ночь предыдущего дня
        if (start.toLocalTime().isBefore(LocalTime.of(6, 0))) {
            return start.toLocalDate().minusDays(1);
        }
        // Если заснул вечером и проснулся на следующий день — ночь дня засыпания
        if (end.toLocalDate().isAfter(start.toLocalDate())) {
            return start.toLocalDate();
        }
        // Иначе (дневной сон) — относим к предыдущей ночи (не будет ночной при проверке)
        return start.toLocalDate().minusDays(1);
    }

    /** Общее число ночей в интервале логирования. */
    private long countTotalNights(LocalDateTime firstStart, LocalDateTime lastEnd) {
        // Определяем первую потенциальную ночь
        LocalDate firstNight;
        if (firstStart.toLocalTime().isAfter(LocalTime.NOON)) {
            firstNight = firstStart.toLocalDate().plusDays(1);
        } else {
            firstNight = firstStart.toLocalDate();
        }

        // Последняя потенциальная ночь — та, что покрывается концом логирования
        LocalDate lastNight;
        if (lastEnd.toLocalTime().isAfter(LocalTime.of(6, 0))) {
            lastNight = lastEnd.toLocalDate();
        } else {
            lastNight = lastEnd.toLocalDate().minusDays(1);
        }

        if (lastNight.isBefore(firstNight)) {
            return 0;
        }
        return Period.between(firstNight, lastNight).getDays() + 1;
    }
}