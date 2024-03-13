package com.test.workerplanning;

import com.test.workerplanning.adapters.dataaccess.repository.WorkerJpaRepository;
import com.test.workerplanning.domain.application_service.dto.CreateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.ScheduleShiftRequest;
import com.test.workerplanning.domain.application_service.dto.ScheduledShiftResponse;
import com.test.workerplanning.domain.application_service.dto.ShiftDto;
import com.test.workerplanning.domain.application_service.dto.UpdateWorkerRequest;
import com.test.workerplanning.domain.application_service.dto.WorkerDto;
import com.test.workerplanning.domain.application_service.dto.WorkerScheduleResponse;
import com.test.workerplanning.domain.core.model.Shift;
import com.test.workerplanning.domain.core.model.ShiftType;
import com.test.workerplanning.domain.core.model.Worker;
import com.test.workerplanning.fixtures.WorkerMother;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static com.test.workerplanning.fixtures.WorkerMother.JOHN_DOE_ID;
import static io.restassured.RestAssured.given;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@ActiveProfiles("db-postgres")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(type = POSTGRES, provider = DatabaseProvider.ZONKY)
public class WorkPlannerAcceptanceTests {

    private static final String WORKER_BASE_URL = "/workers";
    private static final String SCHEDULE_SHIFT_URL = WORKER_BASE_URL + "/shifts";
    private static final LocalDate TODAY = LocalDate.now();

    @LocalServerPort
    private int port;

    @Autowired
    private WorkerJpaRepository workerJpaRepository;

    @BeforeEach
    void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        workerJpaRepository.deleteAll();
    }

    @Test
    void return201_whenCreatingNewWorker() {
        Worker newWorker = WorkerMother.johnDoe();

        WorkerDto createdWorkerDto = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(toCreateRequest(newWorker))

                .when()
                    .post(WORKER_BASE_URL)

                .then()
                    .statusCode(CREATED.value())
                    .extract().as(WorkerDto.class);

        assertThat(createdWorkerDto.getWorkerId()).isNotNull();
        assertThat(createdWorkerDto.getFirstName()).isEqualTo(newWorker.getFirstName());
        assertThat(createdWorkerDto.getLastName()).isEqualTo(newWorker.getLastName());
        assertThat(createdWorkerDto.getEmail()).isEqualTo(newWorker.getEmail());
        assertThat(createdWorkerDto.getVersion()).isEqualTo(0);

        Worker persistedWorker = workerJpaRepository.findWorkerById(createdWorkerDto.getWorkerId()).orElseThrow();
        assertThat(persistedWorker.getFirstName()).isEqualTo(createdWorkerDto.getFirstName());
        assertThat(persistedWorker.getLastName()).isEqualTo(createdWorkerDto.getLastName());
        assertThat(persistedWorker.getEmail()).isEqualTo(createdWorkerDto.getEmail());
        assertThat(persistedWorker.getVersion()).isEqualTo(0);
    }

    @Test
    void returnNotFound_whenTryingToUpdateUnknownWorker() {
        Worker unknownWorker = WorkerMother.johnDoe();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(toUpdateRequest(unknownWorker))

                .when()
                    .put(WORKER_BASE_URL)

                .then()
                    .statusCode(NOT_FOUND.value())
                    .body("message", equalTo("Worker with id: %s cannot be found.".formatted(unknownWorker.getId())));
    }

    @Test
    void return200_whenTryingToUpdateExistingWorker() {
        Worker johnDoe = WorkerMother.johnDoe();
        persistWorker(johnDoe);

        Worker updatedWorkerDto = johnDoe.toBuilder()
                .firstName("Johny")
                .lastName("Smith")
                .email("johny.smith@gmail.com")
                .build();

        var updatedWorkerResponse = updateWorker(updatedWorkerDto)
                .statusCode(OK.value())
                .extract().as(WorkerDto.class);

        assertThat(updatedWorkerResponse.getWorkerId()).isEqualTo(johnDoe.getId());
        assertThat(updatedWorkerResponse.getFirstName()).isEqualTo(updatedWorkerDto.getFirstName());
        assertThat(updatedWorkerResponse.getLastName()).isEqualTo(updatedWorkerDto.getLastName());
        assertThat(updatedWorkerResponse.getEmail()).isEqualTo(updatedWorkerDto.getEmail());
        assertThat(updatedWorkerResponse.getVersion()).isEqualTo(1);

        Worker persistedWorker = workerJpaRepository.findWorkerById(updatedWorkerResponse.getWorkerId()).orElseThrow();
        assertThat(persistedWorker.getFirstName()).isEqualTo(updatedWorkerResponse.getFirstName());
        assertThat(persistedWorker.getLastName()).isEqualTo(updatedWorkerResponse.getLastName());
        assertThat(persistedWorker.getEmail()).isEqualTo(updatedWorkerResponse.getEmail());
        assertThat(persistedWorker.getVersion()).isEqualTo(1);
    }

    @Test
    void scheduleShouldNotChange_whenUpdatingWorker() {
        Worker johnDoe = WorkerMother.johnDoe();
        Shift dayShift = aDayShift();

        johnDoe.addShift(dayShift);
        persistWorker(johnDoe);

        Worker updatedWorker = johnDoe.toBuilder()
                .firstName("Johny")
                .lastName("Smith")
                .email("johny.smith@gmail.com")
                .build();

        updateWorker(updatedWorker).statusCode(OK.value());

        var worker = workerJpaRepository.findWorkerById(JOHN_DOE_ID).orElseThrow();
        assertThat(worker.getVersion()).isEqualTo(1);
        assertThat(worker.getShifts()).hasSize(1);
        Shift shift = worker.getShifts().get(0);
        assertThat(shift.getId()).isEqualTo(dayShift.getId());
        assertThat(shift.getDay()).isEqualTo(TODAY);
        assertThat(shift.getType()).isEqualTo(ShiftType.DAY);
    }

    @Test
    void returnConflict_whenUpdatingWorkerWithStaleVersion() {
        Worker johnDoe = WorkerMother.johnDoe();
        persistWorker(johnDoe);

        var updatedWorker1 = johnDoe.toBuilder()
                .firstName("Johny")
                .lastName("Smith")
                .email("johny.smith1@gmail.com")
                .build();

        var updatedWorker2 = johnDoe.toBuilder()
                .firstName("Johny")
                .lastName("Smith")
                .email("johny.smith2@gmail.com")
                .build();

        updateWorker(updatedWorker1).statusCode(OK.value());

        updateWorker(updatedWorker2)
                .statusCode(CONFLICT.value())
                .body("message", equalTo("Worker was updated by another transaction. Please try again!"));

        var worker = workerJpaRepository.findWorkerById(JOHN_DOE_ID).orElseThrow();
        assertThat(worker.getVersion()).isEqualTo(1);
    }

    @ParameterizedTest(name = "Schedule a new {0} shift on timeslot {1}")
    @CsvSource(textBlock = """
            DAY,    8-16
            MID,    16-24
            NIGHT,   0-8
            """)
    void return201_whenSchedulingEightHourShiftsBasedOnType(String shiftType, String timeslot) {
        persistWorker(WorkerMother.johnDoe());
        var newShiftRequest = new ScheduleShiftRequest(JOHN_DOE_ID, TODAY, ShiftType.valueOf(shiftType));

        var planedShift = scheduleAShift(newShiftRequest);

        assertThat(planedShift.getShiftId()).isNotNull();
        assertThat(planedShift.getWorkerId()).isEqualTo(JOHN_DOE_ID);
        assertThat(planedShift.getDay()).isEqualTo(TODAY);
        assertThat(planedShift.getType()).isEqualTo(shiftType);
        assertThat(planedShift.getTimeslot()).isEqualTo(timeslot);
    }

    @Test
    void returnBadRequest_whenTryingToScheduleTwoShiftsInTheSameDayForTheSameWorker() {
        persistWorker(WorkerMother.johnDoe());
        var sameDayMidShift = new ScheduleShiftRequest(JOHN_DOE_ID, TODAY, ShiftType.MID);
        var sameDayNightShift = new ScheduleShiftRequest(JOHN_DOE_ID, TODAY, ShiftType.NIGHT);

        scheduleAShift(sameDayMidShift);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sameDayNightShift)

                .when()
                    .post(SCHEDULE_SHIFT_URL)

                .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("message", equalTo(
                        "Worker %s already has a shift scheduled on %s. Please try a different day.".formatted(JOHN_DOE_ID, TODAY)));
    }

    @Test
    void returnWorkerScheduleWithoutShifts_whenNoShiftsWerePlanned() {
        Worker johnDoe = WorkerMother.johnDoe();
        persistWorker(johnDoe);

        var johnDoeSchedule = getWorkerSchedule(JOHN_DOE_ID);

        assertThat(johnDoeSchedule.getFirstName()).isEqualTo(johnDoe.getFirstName());
        assertThat(johnDoeSchedule.getLastName()).isEqualTo(johnDoe.getLastName());
        assertThat(johnDoeSchedule.getEmail()).isEqualTo(johnDoe.getEmail());
        assertThat(johnDoeSchedule.getVersion()).isEqualTo(0);
        assertThat(johnDoeSchedule.getShifts()).isEmpty();
    }

    @Test
    void returnWorkerScheduleSortedByDay_whenWorkerIdIsFound() {
        Worker johnDoe = WorkerMother.johnDoe();
        persistWorker(johnDoe);

        var dayShift = scheduleAShift(new ScheduleShiftRequest(JOHN_DOE_ID, TODAY.plusDays(2), ShiftType.DAY));
        var midShift = scheduleAShift(new ScheduleShiftRequest(JOHN_DOE_ID, TODAY, ShiftType.MID));
        var nightShift = scheduleAShift(new ScheduleShiftRequest(JOHN_DOE_ID, TODAY.plusDays(7), ShiftType.NIGHT));

        var johnDoeSchedule = getWorkerSchedule(JOHN_DOE_ID);

        assertThat(johnDoeSchedule.getFirstName()).isEqualTo(johnDoe.getFirstName());
        assertThat(johnDoeSchedule.getLastName()).isEqualTo(johnDoe.getLastName());
        assertThat(johnDoeSchedule.getShifts()).containsExactly(
                shiftDtoFromScheduledShift(midShift),
                shiftDtoFromScheduledShift(dayShift),
                shiftDtoFromScheduledShift(nightShift)
        );
    }

    private static ScheduledShiftResponse scheduleAShift(ScheduleShiftRequest scheduleShiftRequest) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(scheduleShiftRequest)

                .when()
                    .post(SCHEDULE_SHIFT_URL)

                .then()
                    .statusCode(CREATED.value())
                    .extract().as(ScheduledShiftResponse.class);
    }

    private static WorkerScheduleResponse getWorkerSchedule(UUID workerId) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)

                .when()
                    .get("%s?workerId=%s".formatted(WORKER_BASE_URL, workerId))

                .then()
                    .statusCode(OK.value())
                    .extract().as(WorkerScheduleResponse.class);
    }

    private ValidatableResponse updateWorker(Worker updatedWorker) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(toUpdateRequest(updatedWorker))

                .when()
                    .put(WORKER_BASE_URL)

                .then();
    }

    private void persistWorker(Worker worker) {
        workerJpaRepository.save(worker);
    }

    private CreateWorkerRequest toCreateRequest(Worker worker) {
        return CreateWorkerRequest.builder()
                .firstName(worker.getFirstName())
                .lastName(worker.getLastName())
                .email(worker.getEmail())
                .build();
    }

    private UpdateWorkerRequest toUpdateRequest(Worker worker) {
        return UpdateWorkerRequest.builder()
                .workerId(worker.getId())
                .firstName(worker.getFirstName())
                .lastName(worker.getLastName())
                .email(worker.getEmail())
                .version(isNull(worker.getVersion()) ? 0 : worker.getVersion())
                .build();
    }

    private ShiftDto shiftDtoFromScheduledShift(ScheduledShiftResponse scheduledShift) {
        return ShiftDto.builder()
                .id(scheduledShift.getShiftId())
                .day(scheduledShift.getDay())
                .type(scheduledShift.getType())
                .timeslot(scheduledShift.getTimeslot())
                .build();
    }

    private static Shift aDayShift() {
        return Shift.builder()
                .id(UUID.randomUUID())
                .day(TODAY)
                .type(ShiftType.DAY)
                .build();
    }

}
