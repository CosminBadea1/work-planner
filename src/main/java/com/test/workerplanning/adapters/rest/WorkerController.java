package com.test.workerplanning.adapters.rest;

import com.test.workerplanning.domain.application_service.dto.CreateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.ScheduleShiftRequest;
import com.test.workerplanning.domain.application_service.dto.ScheduledShiftResponse;
import com.test.workerplanning.domain.application_service.dto.UpdateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.WorkerDto;
import com.test.workerplanning.domain.application_service.dto.WorkerScheduleResponse;
import com.test.workerplanning.domain.application_service.ports.input.WorkerApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/workers")
public class WorkerController {

    private final WorkerApplicationService workerApplicationService;

    @Operation(summary = "Create a new worker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Worker was successfully created.", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = WorkerDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping
    public ResponseEntity<WorkerDto> createWorker(@RequestBody @Validated CreateWorkerRequest createWorkerRequest) {
        log.info("Creating a new worker firstName: {}, lastName: {}", createWorkerRequest.getFirstName(), createWorkerRequest.getLastName());

        WorkerDto workerDto = workerApplicationService.create(createWorkerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workerDto);
    }

    @Operation(summary = "Update worker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Worker was successfully updated.", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = WorkerDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
            @ApiResponse(responseCode = "404", description = "Worker not found."),
            @ApiResponse(responseCode = "409", description = "Conflict."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PutMapping
    public ResponseEntity<WorkerDto> updateWorker(@RequestBody @Validated UpdateWorkerRequest updateWorkerRequest) {
        log.info("Updating worker with id: {}", updateWorkerRequest.getWorkerId());

        WorkerDto workerDto = workerApplicationService.update(updateWorkerRequest);
        return ResponseEntity.ok(workerDto);
    }

    @Operation(summary = "Get worker schedule by worker ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = WorkerScheduleResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
            @ApiResponse(responseCode = "404", description = "Worker not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping
    public ResponseEntity<WorkerScheduleResponse> getWorkerSchedule(@Parameter(description = "Worker ID", example = "d215b5f8-0249-4dc5-89a3-51fd148cfb41", required = true)
                                                                    @RequestParam @Validated @NotNull UUID workerId) {
        log.info("Returning schedule by workerId: {}", workerId);
        WorkerScheduleResponse workerSchedule = workerApplicationService.findWorkerSchedule(workerId);
        return ResponseEntity.ok(workerSchedule);
    }

    @Operation(summary = "Schedule a new worker shift.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shift was successfully created.", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ScheduledShiftResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
            @ApiResponse(responseCode = "404", description = "Worker not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping("/shifts")
    public ResponseEntity<ScheduledShiftResponse> scheduleShift(@RequestBody @Validated ScheduleShiftRequest scheduleShiftRequest) {
        log.info("Scheduling new shift for workerId: {}", scheduleShiftRequest.getWorkerId());

        ScheduledShiftResponse scheduledShiftResponse = workerApplicationService.scheduleShift(scheduleShiftRequest);
        log.info("A {} shift has been scheduled for workerId: {} on: {}", scheduledShiftResponse.getType(),
                scheduledShiftResponse.getWorkerId(), scheduledShiftResponse.getDay());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduledShiftResponse);
    }

}
