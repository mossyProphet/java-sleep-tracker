package ru.yandex.practicum.sleeptracker;

import java.util.List;

@FunctionalInterface
public interface Function {
    SleepAnalysisResult<?> apply(List<SleepingSession> sessions);
}
