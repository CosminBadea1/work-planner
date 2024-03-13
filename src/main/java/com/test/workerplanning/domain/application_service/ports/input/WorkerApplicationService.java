package com.test.workerplanning.domain.application_service.ports.input;

import com.test.workerplanning.domain.application_service.dto.CreateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.ScheduleShiftRequest;
import com.test.workerplanning.domain.application_service.dto.ScheduledShiftResponse;
import com.test.workerplanning.domain.application_service.dto.UpdateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.WorkerDto;
import com.test.workerplanning.domain.application_service.dto.WorkerScheduleResponse;

import java.util.UUID;

public interface WorkerApplicationService {

    WorkerDto create(CreateWorkerRequest createWorkerRequest);

    WorkerDto update(UpdateWorkerRequest updateWorkerRequest);

    ScheduledShiftResponse scheduleShift(ScheduleShiftRequest scheduleShiftRequest);

    WorkerScheduleResponse findWorkerSchedule(UUID workerId);
}
