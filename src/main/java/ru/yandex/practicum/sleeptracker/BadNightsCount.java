package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class BadNightsCount implements Function{
    @Override
    public SleepAnalysisResult<?> apply(List<SleepingSession> sessions) {
        long count = sessions.stream()
                .filter(s -> s.getSleepQuality() == SleepQuality.BAD)
                .count();
        return new SleepAnalysisResult<>("Сессий с плохим качеством сна", count);
    }
}
