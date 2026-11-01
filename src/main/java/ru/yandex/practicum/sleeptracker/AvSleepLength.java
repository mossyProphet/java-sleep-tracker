package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.OptionalDouble;

public class AvSleepLength implements Function{
    @Override
    public SleepAnalysisResult<?> apply(List<SleepingSession> sessions){
        OptionalDouble av = sessions.stream().mapToLong(SleepingSession::durationMinutes).average();
        return new SleepAnalysisResult<>("Средняя продолжительность сессии (мин)", av);
    }
}
