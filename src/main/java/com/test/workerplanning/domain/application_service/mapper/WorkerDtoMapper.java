package com.test.workerplanning.domain.application_service.mapper;

import com.test.workerplanning.domain.application_service.dto.CreateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.ScheduleShiftRequest;
import com.test.workerplanning.domain.application_service.dto.ScheduledShiftResponse;
import com.test.workerplanning.domain.application_service.dto.ShiftDto;
import com.test.workerplanning.domain.application_service.dto.UpdateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.WorkerDto;
import com.test.workerplanning.domain.application_service.dto.WorkerScheduleResponse;
import com.test.workerplanning.domain.core.model.Shift;
import com.test.workerplanning.domain.core.model.Worker;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.UUID;

@Component
public class WorkerDtoMapper {

    public Worker createWorkerRequestToWorker(CreateWorkerRequest createWorkerRequest) {
        return Worker.builder()
                .firstName(createWorkerRequest.getFirstName())
                .lastName(createWorkerRequest.getLastName())
                .email(createWorkerRequest.getEmail())
                .build();
    }

    public Worker updateWorkerRequestToWorker(UpdateWorkerRequest updateWorkerRequest) {
        return Worker.builder()
                .id(updateWorkerRequest.getWorkerId())
                .firstName(updateWorkerRequest.getFirstName())
                .lastName(updateWorkerRequest.getLastName())
                .email(updateWorkerRequest.getEmail())
                .version(updateWorkerRequest.getVersion())
                .build();
    }

    public WorkerDto workerToDto(Worker worker) {
        return WorkerDto.builder()
                .workerId(worker.getId())
                .firstName(worker.getFirstName())
                .lastName(worker.getLastName())
                .email(worker.getEmail())
                .version(worker.getVersion())
                .build();
    }

    public WorkerScheduleResponse workerToWorkerScheduleResponse(Worker worker) {
        return WorkerScheduleResponse.builder()
                .workerId(worker.getId())
                .firstName(worker.getFirstName())
                .lastName(worker.getLastName())
                .email(worker.getEmail())
                .version(worker.getVersion())
                .shifts(worker.getShifts().stream()
                        .sorted(Comparator.comparing(Shift::getDay))
                        .map(this::shiftToDto)
                        .toList())
                .build();
    }

    public Shift scheduleShiftCommandToShift(ScheduleShiftRequest scheduleShiftRequest) {
        return Shift.builder()
                .id(UUID.randomUUID())
                .day(scheduleShiftRequest.getDay())
                .type(scheduleShiftRequest.getShiftType())
                .build();
    }

    public ScheduledShiftResponse shiftToScheduledShiftResponse(Shift shift) {
        return ScheduledShiftResponse.builder()
                .shiftId(shift.getId())
                .workerId(shift.getWorker().getId())
                .day(shift.getDay())
                .type(shift.getType().name())
                .timeslot(shift.getType().timeslot())
                .build();
    }

    private ShiftDto shiftToDto(Shift shift) {
        return ShiftDto.builder()
                .id(shift.getId())
                .day(shift.getDay())
                .type(shift.getType().name())
                .timeslot(shift.getType().timeslot())
                .build();
    }
}
