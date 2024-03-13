package com.test.workerplanning.domain.application_service.dto;

import com.test.workerplanning.domain.core.model.ShiftType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleShiftRequest {

    @NotNull
    @Schema(description = "Worker id", example = "d215b5f8-0249-4dc5-89a3-51fd148cfb41")
    private UUID workerId;

    @FutureOrPresent
    @Schema(description = "Present or future day", example = "2030-06-10")
    private LocalDate day;

    @NotNull
    @Schema(description = "Shift type")
    private ShiftType shiftType;
}
