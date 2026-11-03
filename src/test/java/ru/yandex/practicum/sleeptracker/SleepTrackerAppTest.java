package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleepTrackerAppTest {

    // ---------- helpers ----------

    private static SleepingSession session(String start, String end) {
        return session(start, end, SleepQuality.GOOD);
    }

    private static SleepingSession session(String start, String end, SleepQuality quality) {
        return new SleepingSession(LocalDateTime.parse(start), LocalDateTime.parse(end), quality);
    }

    private static Object valueOf(Function f, List<SleepingSession> sessions) {
        return f.apply(sessions).getValue();
    }

    // ---------- total sessions ----------

    @Test
    void countsSessions() {
        var sessions = List.of(
                session("2025-10-01T22:15", "2025-10-02T08:00"),
                session("2025-10-02T23:00", "2025-10-03T08:00"),
                session("2025-10-03T14:30", "2025-10-03T15:20"));
        assertEquals(3, valueOf(new TotalSessionsFunction(), sessions));
    }

    // ---------- duration metrics ----------

    @Test
    void computesMinMaxAvgDuration() {
        var sessions = List.of(
                session("2025-10-01T22:00", "2025-10-01T23:00"),   // 60
                session("2025-10-02T00:00", "2025-10-02T02:00"),   // 120
                session("2025-10-03T01:00", "2025-10-03T01:30"));  // 30
        assertEquals(30L,   valueOf(new MinSleepLength(), sessions));
        assertEquals(120L,  valueOf(new MaxSleepLength(), sessions));
        assertEquals(70.0,  valueOf(new AvSleepLength(), sessions));
    }

    // ---------- bad quality ----------

    @Test
    void countsBadQualitySessions() {
        var sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T22:00", "2025-10-03T06:00", SleepQuality.BAD),
                session("2025-10-03T22:00", "2025-10-04T06:00", SleepQuality.BAD),
                session("2025-10-04T22:00", "2025-10-05T06:00", SleepQuality.NORMAL));
        assertEquals(2L, valueOf(new BadNightsCount(), sessions));
    }

    // ---------- sleepless nights ----------

    @Test
    void detectsSleeplessNightBetweenSessions() {
        // Nights 02.10 and 04.10 covered; 03.10 is missing.
        var sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T07:00"),
                session("2025-10-03T23:00", "2025-10-04T07:00"));
        assertEquals(1L, valueOf(new SleeplessNights(), sessions));
    }

    @Test
    void daySleepDoesNotCoverNight() {
        // 07:00–11:00 naps never overlap [00:00, 06:00].
        var sessions = List.of(
                session("2025-10-01T07:00", "2025-10-01T11:00"),
                session("2025-10-02T07:00", "2025-10-02T11:00"));
        assertEquals(2L, valueOf(new SleeplessNights(), sessions));
    }

    @Test
    void sessionStartingAfterNoonShiftsFirstNight() {
        // Log starts 01.10 at 14:00 → first candidate night is 02.10,
        // which is then covered by the overnight session → 0 sleepless.
        var sessions = List.of(
                session("2025-10-01T14:00", "2025-10-01T15:00"),
                session("2025-10-01T23:00", "2025-10-02T07:00"));
        assertEquals(0L, valueOf(new SleeplessNights(), sessions));
    }

    // ---------- chronotype ----------

    @Test
    void owlDominant() {
        var sessions = List.of(
                session("2025-10-01T23:30", "2025-10-02T09:30"),   // OWL
                session("2025-10-02T23:45", "2025-10-03T10:00"),   // OWL
                session("2025-10-03T21:00", "2025-10-04T06:00"));  // LARK
        assertEquals(Chronotype.OWL, valueOf(new ChronotypeFunction(), sessions));
    }

    @Test
    void tieResolvedToPigeon() {
        // 1 OWL + 1 LARK → tie → PIGEON
        var sessions = List.of(
                session("2025-10-01T23:30", "2025-10-02T09:30"),   // OWL
                session("2025-10-02T21:00", "2025-10-03T06:00"));  // LARK
        assertEquals(Chronotype.PIGEON, valueOf(new ChronotypeFunction(), sessions));
    }

    @Test
    void daySessionsIgnoredForChronotype() {
        var sessions = List.of(
                session("2025-10-01T14:00", "2025-10-01T15:00"),   // day nap — ignored
                session("2025-10-02T21:00", "2025-10-03T06:00"));  // LARK
        assertEquals(Chronotype.LARK, valueOf(new ChronotypeFunction(), sessions));
    }
}