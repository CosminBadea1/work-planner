package com.test.workerplanning.domain.application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateWorkerRequest {

    @NotNull
    @Schema(description = "Worker id", example = "d215b5f8-0249-4dc5-89a3-51fd148cfb41")
    private UUID workerId;

    @NotBlank
    @Schema(description = "First Name", example = "John")
    private String firstName;

    @NotBlank
    @Schema(description = "Last Name", example = "Doe")
    private String lastName;

    @Email
    @Schema(description = "Email", example = "john.doe@gmail.com")
    private String email;

    @NotNull
    @Schema(description = "Version", example = "1")
    private Long version;
}
