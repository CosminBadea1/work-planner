package com.test.workerplanning.domain.core.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workers")
public class Worker {

    @Id
    @Setter(AccessLevel.PRIVATE)
    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    @OneToMany(
            mappedBy = "worker",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Shift> shifts = new ArrayList<>();

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker that = (Worker) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void initialize() {
        id = UUID.randomUUID();
    }

    public void addShift(Shift shift) {
        shifts.add(shift);
        shift.setWorker(this);
    }

    public boolean hasShiftAlreadyPlanned(LocalDate day) {
        return shifts.stream()
                .map(Shift::getDay)
                .anyMatch(day::equals);
    }
}
