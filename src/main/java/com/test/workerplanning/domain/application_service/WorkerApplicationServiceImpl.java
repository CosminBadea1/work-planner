package com.test.workerplanning.domain.application_service;

import com.test.workerplanning.domain.application_service.dto.CreateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.ScheduleShiftRequest;
import com.test.workerplanning.domain.application_service.dto.ScheduledShiftResponse;
import com.test.workerplanning.domain.application_service.dto.UpdateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.WorkerDto;
import com.test.workerplanning.domain.application_service.dto.WorkerScheduleResponse;
import com.test.workerplanning.domain.application_service.mapper.WorkerDtoMapper;
import com.test.workerplanning.domain.application_service.ports.input.WorkerApplicationService;
import com.test.workerplanning.domain.application_service.ports.output.WorkerRepository;
import com.test.workerplanning.domain.core.exception.ShiftDomainException;
import com.test.workerplanning.domain.core.exception.WorkerNotFoundException;
import com.test.workerplanning.domain.core.model.Shift;
import com.test.workerplanning.domain.core.model.Worker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerApplicationServiceImpl implements WorkerApplicationService {

    private final WorkerRepository workerRepository;
    private final WorkerDtoMapper workerDtoMapper;

    @Override
    public WorkerDto create(CreateWorkerRequest createWorkerRequest) {
        Worker newWorker = workerDtoMapper.createWorkerRequestToWorker(createWorkerRequest);
        newWorker.initialize();
        return workerDtoMapper.workerToDto(workerRepository.save(newWorker));
    }

    @Override
    public WorkerDto update(UpdateWorkerRequest updateWorkerRequest) {
        Worker existingWorker = checkThatWorkerExists(updateWorkerRequest.getWorkerId());
        Worker updatedWorker = workerDtoMapper.updateWorkerRequestToWorker(updateWorkerRequest);
        updatedWorker.setShifts(existingWorker.getShifts());
        return workerDtoMapper.workerToDto(workerRepository.save(updatedWorker));
    }

    @Override
    public WorkerScheduleResponse findWorkerSchedule(UUID workerId) {
        Worker worker = checkThatWorkerExists(workerId);
        return workerDtoMapper.workerToWorkerScheduleResponse(worker);
    }

    @Override
    public ScheduledShiftResponse scheduleShift(ScheduleShiftRequest scheduleShiftRequest) {
        Worker worker = checkThatWorkerExists(scheduleShiftRequest.getWorkerId());
        checkThatWorkerIsEligibleForShift(worker, scheduleShiftRequest);

        Shift newShift = workerDtoMapper.scheduleShiftCommandToShift(scheduleShiftRequest);
        worker.addShift(newShift);
        workerRepository.save(worker);

        return workerDtoMapper.shiftToScheduledShiftResponse(newShift);
    }

    private Worker checkThatWorkerExists(UUID workerId) {
        return workerRepository.findById(workerId)
                .orElseThrow(() -> new WorkerNotFoundException("Worker with id: %s cannot be found.".formatted(workerId)));
    }

    private void checkThatWorkerIsEligibleForShift(Worker worker, ScheduleShiftRequest scheduleShiftRequest) {
        if (worker.hasShiftAlreadyPlanned(scheduleShiftRequest.getDay())) {
            throw new ShiftDomainException("Worker %s already has a shift scheduled on %s. Please try a different day."
                    .formatted(scheduleShiftRequest.getWorkerId(), scheduleShiftRequest.getDay()));
        }
    }

}
