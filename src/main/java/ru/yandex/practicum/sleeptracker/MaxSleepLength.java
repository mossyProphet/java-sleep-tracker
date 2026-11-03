package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MaxSleepLength implements Function {
    @Override
    public SleepAnalysisResult<?> apply(List<SleepingSession> sessions) {
        long max = sessions.stream().mapToLong(SleepingSession::durationMinutes).max().orElse(1000000000);
        return new SleepAnalysisResult<>("Максимальная продолжительность сессии (мин)", max);
    }
}
