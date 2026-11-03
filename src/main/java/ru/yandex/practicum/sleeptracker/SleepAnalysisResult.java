package ru.yandex.practicum.sleeptracker;

public class SleepAnalysisResult<T> {
    private final String description;
    private final T value;

    public SleepAnalysisResult(String description, T value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return description + ": " + value;
    }
}