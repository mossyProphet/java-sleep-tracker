package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MinSleepLength implements Function {
    @Override
    public SleepAnalysisResult<?> apply(List<SleepingSession> sessions) {
        long min = sessions.stream().mapToLong(SleepingSession::durationMinutes).min().orElse(0);
        return new SleepAnalysisResult<>("Минимальная продолжительность сессии (мин)", min);
    }
}
