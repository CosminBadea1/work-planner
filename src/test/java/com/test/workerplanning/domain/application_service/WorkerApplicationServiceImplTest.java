package com.test.workerplanning.domain.application_service;

import com.test.workerplanning.domain.application_service.dto.CreateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.UpdateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.WorkerDto;
import com.test.workerplanning.domain.application_service.mapper.WorkerDtoMapper;
import com.test.workerplanning.domain.application_service.ports.output.WorkerRepository;
import com.test.workerplanning.domain.core.exception.WorkerNotFoundException;
import com.test.workerplanning.domain.core.model.Worker;
import com.test.workerplanning.fixtures.WorkerMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.test.workerplanning.fixtures.WorkerMother.DOE;
import static com.test.workerplanning.fixtures.WorkerMother.JOHN;
import static com.test.workerplanning.fixtures.WorkerMother.JOHN_DOE_EMAIL;
import static com.test.workerplanning.fixtures.WorkerMother.JOHN_DOE_ID;
import static com.test.workerplanning.fixtures.WorkerMother.withDayShift;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WorkerApplicationServiceImplTest {

    private static final String UPDATED_LAST_NAME = "Smith";
    private static final String UPDATED_EMAIL = "john.smith@gmail.com";

    @Mock
    private WorkerRepository workerRepository;

    private WorkerApplicationServiceImpl underTest;

    @Captor
    ArgumentCaptor<Worker> workerCaptor;

    @BeforeEach
    void setUp() {
        underTest = new WorkerApplicationServiceImpl(workerRepository, new WorkerDtoMapper());
    }

    @Test
    void shouldCreateANewWorker() {
        CreateWorkerRequest createRequest = CreateWorkerRequest.builder()
                .firstName(JOHN)
                .lastName(DOE)
                .email(JOHN_DOE_EMAIL)
                .build();
        given(workerRepository.save(workerCaptor.capture())).willReturn(WorkerMother.johnDoe());

        WorkerDto response = underTest.create(createRequest);

        assertThat(workerCaptor.getValue().getId()).isNotNull();
        assertThat(workerCaptor.getValue().getFirstName()).isEqualTo(createRequest.getFirstName());
        assertThat(workerCaptor.getValue().getLastName()).isEqualTo(createRequest.getLastName());
        assertThat(workerCaptor.getValue().getEmail()).isEqualTo(createRequest.getEmail());
        assertThat(workerCaptor.getValue().getVersion()).isNull();

        assertThat(response.getWorkerId()).isEqualTo(JOHN_DOE_ID);
        assertThat(response.getFirstName()).isEqualTo(JOHN);
        assertThat(response.getLastName()).isEqualTo(DOE);
        assertThat(response.getEmail()).isEqualTo(JOHN_DOE_EMAIL);
        assertThat(response.getVersion()).isEqualTo(0L);
    }

    @Test
    void shouldUpdateExistingWorker() {
        Worker existingWorker = withDayShift(WorkerMother.johnDoe());
        given(workerRepository.findById(JOHN_DOE_ID)).willReturn(Optional.of(existingWorker));
        given(workerRepository.save(workerCaptor.capture())).willReturn(buildUpdatedWorker(existingWorker));

        UpdateWorkerRequest updateRequest = buildUpdateWorkerRequest();
        WorkerDto response = underTest.update(updateRequest);

        assertThat(workerCaptor.getValue().getId()).isEqualTo(updateRequest.getWorkerId());
        assertThat(workerCaptor.getValue().getFirstName()).isEqualTo(updateRequest.getFirstName());
        assertThat(workerCaptor.getValue().getLastName()).isEqualTo(updateRequest.getLastName());
        assertThat(workerCaptor.getValue().getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(workerCaptor.getValue().getVersion()).isEqualTo(0);
        assertThat(workerCaptor.getValue().getShifts()).usingRecursiveAssertion()
                .isEqualTo(existingWorker.getShifts());

        assertThat(response.getWorkerId()).isEqualTo(JOHN_DOE_ID);
        assertThat(response.getFirstName()).isEqualTo(JOHN);
        assertThat(response.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(response.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(response.getVersion()).isEqualTo(1L);
    }

    @Test
    void throwWorkerNotFoundException_whenWorkerIdNotFound() {
        given(workerRepository.findById(JOHN_DOE_ID)).willReturn(Optional.empty());
        UpdateWorkerRequest updateWorkerRequest = buildUpdateWorkerRequest();

        WorkerNotFoundException ex = assertThrows(WorkerNotFoundException.class,
                () -> underTest.update(updateWorkerRequest));

        assertThat(ex.getMessage()).isEqualTo("Worker with id: %s cannot be found.".formatted(JOHN_DOE_ID));
    }

    private static UpdateWorkerRequest buildUpdateWorkerRequest() {
        return UpdateWorkerRequest.builder()
                .workerId(JOHN_DOE_ID)
                .firstName(JOHN)
                .lastName(UPDATED_LAST_NAME)
                .email(UPDATED_EMAIL)
                .version(0L)
                .build();
    }

    private static Worker buildUpdatedWorker(Worker existing) {
        return existing.toBuilder()
                .lastName("Smith")
                .email( "john.smith@gmail.com")
                .version(1L)
                .build();
    }
}
