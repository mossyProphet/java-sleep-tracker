package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;

public class SleepingSession {
    private final LocalDateTime sleepStart;
    private final LocalDateTime sleepEnd;
    private final SleepQuality sleepQuality;

    public SleepingSession(LocalDateTime sleepStart, LocalDateTime sleepEnd, SleepQuality sleepQuality) {
        this.sleepStart = sleepStart;
        this.sleepEnd = sleepEnd;
        this.sleepQuality = sleepQuality;
    }

    public LocalDateTime getSleepStart() { //начало сна
        return sleepStart;
    }

    public LocalDateTime getSleepEnd() { //конец сна
        return sleepEnd;
    }

    public SleepQuality getSleepQuality() {
        return sleepQuality;
    }

    public long durationMinutes() {
        return java.time.Duration.between(sleepStart, sleepEnd).toMinutes();
    }
}
