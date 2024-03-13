package com.test.workerplanning.adapters.rest.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {

    private String code;
    private String message;
}
