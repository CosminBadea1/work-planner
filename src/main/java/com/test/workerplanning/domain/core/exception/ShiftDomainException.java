package com.test.workerplanning.domain.core.exception;

public class ShiftDomainException extends RuntimeException {

   public ShiftDomainException(String message) {
      super(message);
   }

   public ShiftDomainException(String message, Throwable cause) {
      super(message, cause);
   }
}
