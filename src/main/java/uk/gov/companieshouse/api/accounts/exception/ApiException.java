package uk.gov.companieshouse.api.accounts.exception;

import uk.gov.companieshouse.api.accounts.exception.handler.ExceptionMessage;

public final class ApiException extends Exception {
   public final ExceptionMessage exceptionMessage;

   public ApiException(ExceptionMessage exceptionMessage) {
     this.exceptionMessage = exceptionMessage;
   }
}
