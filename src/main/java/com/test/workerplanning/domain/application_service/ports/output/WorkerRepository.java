package com.test.workerplanning.domain.application_service.ports.output;

import java.util.Optional;
import java.util.UUID;
import com.test.workerplanning.domain.core.model.Worker;

public interface WorkerRepository {

    Optional<Worker> findById(UUID workerId);

    Worker save(Worker worker);

}
