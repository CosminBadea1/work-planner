package com.test.workerplanning.adapters.dataaccess;

import com.test.workerplanning.adapters.dataaccess.repository.WorkerJpaRepository;
import com.test.workerplanning.domain.application_service.ports.output.WorkerRepository;
import com.test.workerplanning.domain.core.model.Worker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WorkerJpaAdapter implements WorkerRepository {

    private final WorkerJpaRepository workerJpaRepository;

    @Override
    public Worker save(Worker worker) {
        return workerJpaRepository.saveAndFlush(worker);
    }

    @Override
    public Optional<Worker> findById(UUID workerId) {
        return workerJpaRepository.findWorkerById(workerId);
    }

}
