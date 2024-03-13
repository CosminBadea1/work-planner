package com.test.workerplanning.domain.application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateWorkerRequest {

    @NotBlank
    @Schema(description = "First Name", example = "John")
    private String firstName;

    @NotBlank
    @Schema(description = "Last Name", example = "Doe")
    private String lastName;

    @Email
    @Schema(description = "Email", example = "john.doe@gmail.com")
    private String email;
}
