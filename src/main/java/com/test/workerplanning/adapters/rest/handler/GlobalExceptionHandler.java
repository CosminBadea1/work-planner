package com.test.workerplanning.adapters.rest.handler;

import com.test.workerplanning.domain.core.exception.ShiftDomainException;
import com.test.workerplanning.domain.core.exception.WorkerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ErrorDto(INTERNAL_SERVER_ERROR.getReasonPhrase(), "The server encountered an error. Please try again later!");
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        String violations = extractViolationsFromException(methodArgumentNotValidException);
        log.error(methodArgumentNotValidException.getMessage(), methodArgumentNotValidException);
        return new ErrorDto(BAD_REQUEST.getReasonPhrase(), violations);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingRequestValueException.class, HttpMessageNotReadableException.class})
    public ErrorDto handleArgumentException(Exception argumentException) {
        log.error(argumentException.getMessage(), argumentException);
        return new ErrorDto(BAD_REQUEST.getReasonPhrase(), argumentException.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ShiftDomainException.class)
    public ErrorDto handleDomainException(ShiftDomainException shiftDomainException) {
        log.error(shiftDomainException.getMessage(), shiftDomainException);
        return new ErrorDto(HttpStatus.BAD_REQUEST.getReasonPhrase(), shiftDomainException.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(WorkerNotFoundException.class)
    public ErrorDto handleWorkerNotFoundException(WorkerNotFoundException workerNotFoundException) {
        log.error(workerNotFoundException.getMessage(), workerNotFoundException);
        return new ErrorDto(HttpStatus.NOT_FOUND.getReasonPhrase(), workerNotFoundException.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ErrorDto handleOptimisticLockException(OptimisticLockingFailureException optimisticLockException) {
        log.error(optimisticLockException.getMessage(), optimisticLockException);
        return new ErrorDto(HttpStatus.CONFLICT.getReasonPhrase(), "Worker was updated by another transaction. Please try again!");
    }

    private String extractViolationsFromException(BindException bindingException) {
        return bindingException.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> "%s %s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
            .sorted()
            .collect(joining(" -- "));
    }
}
