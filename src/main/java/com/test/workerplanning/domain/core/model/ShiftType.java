package com.test.workerplanning.domain.core.model;

import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@RequiredArgsConstructor
public enum ShiftType {

    DAY(LocalTime.of(8, 0)),
    MID(LocalTime.of(16, 0)),
    NIGHT(LocalTime.MIDNIGHT);

    private static final int SHIFT_LENGTH_HOURS = 8;

    private final LocalTime startTime;

    public String timeslot() {
        return "%s-%s".formatted(startTime.getHour(), startTime.getHour() + SHIFT_LENGTH_HOURS);
    }

    @Override
    public String toString() {
        return name();
    }
}
