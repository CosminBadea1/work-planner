package com.test.workerplanning.adapters.dataaccess.repository;

import com.test.workerplanning.domain.core.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface WorkerJpaRepository extends JpaRepository<Worker, UUID> {

    @Query("""
            SELECT w FROM Worker w
            LEFT JOIN FETCH w.shifts
            WHERE w.id = ?1
            """)
    Optional<Worker> findWorkerById(UUID id);
}
