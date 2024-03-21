package com.test.workerplanning.fixtures;

import com.test.workerplanning.domain.core.model.Shift;
import com.test.workerplanning.domain.core.model.ShiftType;
import com.test.workerplanning.domain.core.model.Worker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class WorkerMother {

    public static final UUID JOHN_DOE_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    public static final String JOHN = "John";
    public static final String DOE = "Doe";
    public static final String JOHN_DOE_EMAIL = "john.doe@gmail.com";

    public static Worker johnDoe() {
        return Worker.builder()
                .id(JOHN_DOE_ID)
                .firstName(JOHN)
                .lastName(DOE)
                .email(JOHN_DOE_EMAIL)
                .version(0L)
                .shifts(new ArrayList<>())
                .build();
    }

    public static Worker withShift(Worker worker, ShiftType type) {
        worker.addShift(Shift.builder()
                .id(UUID.randomUUID())
                .day(LocalDate.now())
                .type(type)
                .build());
        return worker;
    }

}
