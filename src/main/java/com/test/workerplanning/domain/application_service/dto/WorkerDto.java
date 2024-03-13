package com.test.workerplanning.domain.application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class WorkerDto {

    @Schema(description = "Worker ID", example = "d215b5f8-0249-4dc5-89a3-51fd148cfb41")
    private UUID workerId;

    @Schema(description = "First Name", example = "John")
    private String firstName;

    @Schema(description = "Last Name", example = "Doe")
    private String lastName;

    @Schema(description = "Email", example = "john.doe@gmail.com")
    private String email;

    @Schema(description = "Version", example = "1")
    private long version;
}
