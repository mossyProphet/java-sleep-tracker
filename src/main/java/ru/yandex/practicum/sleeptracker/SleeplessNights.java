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

    private LocalDate nightDateOf(SleepingSession s) {
        LocalDate startDate = s.getSleepStart().toLocalDate();
        LocalDate endDate   = s.getSleepEnd().toLocalDate();
        if (endDate.isAfter(startDate)) {                            // crossed midnight → night of the wake-up date
            return endDate;
        }
        if (s.getSleepStart().toLocalTime().isBefore(LocalTime.of(6, 0))) {
            return startDate;                                        // early-morning sleep
        }
        return null;                                                 // day sleep — no night
    }

    private boolean isNightSession(SleepingSession s) {
        return nightDateOf(s) != null;
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