package com.test.workerplanning.domain.application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ScheduledShiftResponse {

    @Schema(description = "Shift id", example = "caabf740-c575-4861-ad71-af98f1a33942")
    private UUID shiftId;

    @Schema(description = "Worker id", example = "d215b5f8-0249-4dc5-89a3-51fd148cfb41")
    private UUID workerId;

    @Schema(description = "Shift day", example = "2023-06-03")
    private LocalDate day;

    @Schema(description = "Shift type", allowableValues = {"DAY", "MID", "NIGHT"})
    private String type;

    @Schema(description = "Timeslot", allowableValues = {"8-16", "16-24", "0-8"})
    private String timeslot;
}
