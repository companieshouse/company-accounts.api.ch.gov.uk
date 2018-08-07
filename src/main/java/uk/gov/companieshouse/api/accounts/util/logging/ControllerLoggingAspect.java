package uk.gov.companieshouse.api.accounts.util.logging;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.util.AccountsUtility;

@Aspect
@Component
public class ControllerLoggingAspect {

  @Pointcut("execution(* uk.gov.companieshouse.api.accounts.controller.*.*(..))")
  private void controllerMethods() {
    //Defines the point cut for controller methods, the method body kept empty on purpose.
  }

  /**
   * The Around advice for the controller methods
   *
   * Captures the start and end times, there is no attempt to catch exceptions from the proceed as
   * they would be handled by Spring @ExceptionHandler.
   */
  @Around("controllerMethods()")
  public Object logTraceAndStats(ProceedingJoinPoint joinPoint) throws Throwable {
    AccountsLogger logger = getLogger(joinPoint);
    String endMsg;

    logger.logStartOfRequestProcessing();

    long startTime = System.currentTimeMillis();

    Object result;
    //Do not change, the target method will not progress without this.
    result = joinPoint.proceed();

    long responseTime = System.currentTimeMillis() - startTime;
    int statusCode = statusCode(result);

    endMsg = reqCompletedSuccessFully(statusCode)
        ? AccountsUtility.SUCCESS_MSG.value()
        : AccountsUtility.FAILURE_MSG.value();

    logger.logEndOfRequestProcessing(
        statusCode(result),
        responseTime);
    return result;
  }

  private String requestId(HttpServletRequest request) {
    return request.getHeader(AccountsUtility.REQUEST_ID.value());
  }

  private String userId(HttpServletRequest request) {
    return request.getHeader(AccountsUtility.ERIC_IDENTITY.value());
  }

  private String requestPath(HttpServletRequest request) {
    return request.getRequestURI();
  }

  private AccountsLogger getLogger(JoinPoint joinPoint) {
    HttpServletRequest request = getRequest(joinPoint);
    return new AccountsLoggerImpl(requestPath(request), request.getMethod(), requestId(request),
        userId(request));
  }

  /**
   * Helper to get the HttpServletRequest so that we can get the requestID and the userID.
   *
   * Though this should never happen, the IllegalArgumentException is thrown when the controller
   * method doesn't include the HttpServletRequest. Ias it won't be possible to log details such as
   * the UserID.
   */
  private HttpServletRequest getRequest(JoinPoint joinPoint) {
    return (HttpServletRequest) Arrays.stream(joinPoint.getArgs())
        .filter(arg -> arg instanceof HttpServletRequest)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "JoinPoint arguments do not contain a HttpServletRequest"));
  }

  /**
   * The HTTP status code when the request was successfully handled returns an internal server error
   * when the response is null.
   *
   * @param result - Response from the controller.
   */
  private int statusCode(Object result) {
    if (result != null && result instanceof ResponseEntity) {
      return ((ResponseEntity) result).getStatusCode().value();
    }
    return HttpStatus.INTERNAL_SERVER_ERROR.value();
  }

  private boolean reqCompletedSuccessFully(int statusCode) {
    return statusCode == HttpStatus.CREATED.value()
        || statusCode == HttpStatus.NO_CONTENT.value()
        || statusCode == HttpStatus.OK.value()
        ? true : false;
  }
}
