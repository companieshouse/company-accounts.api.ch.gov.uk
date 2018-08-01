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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerLoggingAspect {

  private static final String REQUEST_ID = "X-Request-Id";

  private static final String ERIC_IDENTITY = "Eric-Identity";

  @Pointcut("execution(* uk.gov.companieshouse.api.accounts.controller.*.*(..))")
  private void controllerMethods() {
    //Defines the point cut for controller methods, the method body kept empty on purpose.
  }

  @Around("controllerMethods()")
  public void logTraceAndStats(ProceedingJoinPoint joinPoint) throws Throwable {
    AccountsLogger logger = getLogger(joinPoint);
    String methodName = getMethodName(joinPoint);
    long startTime = System.currentTimeMillis();
    //Do not change, the target method will not progress without this.
    Object result = null;

    try {
      result = joinPoint.proceed();
    } catch (Throwable t) {
      //possible internal server errors
      logger.logError("Error Occured", new Exception(t),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    } finally {
      long responseTime = System.currentTimeMillis() - startTime;
      int statusCode =
          result != null ? statusCode(result) : HttpStatus.INTERNAL_SERVER_ERROR.value();

      logger.logStartOfRequestProcessing(String.format("Start Request %s",
          methodName));

      logger.logEndOfRequestProcessing(String.format("End of Request %s Successfully Completed",
          methodName), statusCode, responseTime);
    }
  }

  private String getMethodName(JoinPoint joinPoint) {
    return String.format("%s.%s",
        joinPoint.getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName());
  }

  private String requestId(HttpServletRequest request) {
    return request.getHeader(REQUEST_ID);
  }

  private String userId(HttpServletRequest request) {
    return request.getHeader(ERIC_IDENTITY);
  }

  private AccountsLogger getLogger(JoinPoint joinPoint) {
    HttpServletRequest request = getRequest(joinPoint);
    return new AccountsLoggerImpl(requestId(request), userId(request), pathVariables(joinPoint));
  }

  /**
   * Helper to get the HttpServletRequest so that we can get the requestID and the userID.
   */
  private HttpServletRequest getRequest(JoinPoint joinPoint) {
    return (HttpServletRequest) Arrays.stream(joinPoint.getArgs())
        .filter(arg -> arg instanceof HttpServletRequest)
        .collect(toList()).get(0);
  }

  /**
   * path variables such as transaction_id, account_it etc as a list of String.
   */
  private List<String> pathVariables(JoinPoint joinPoint) {
    return Arrays.stream(joinPoint.getArgs())
        .filter(arg -> arg instanceof String)
        .map(arg -> arg.toString())
        .collect(toList());
  }

  /**
   * The HTTP status code when the request was successfully handled
   * returns an internal server error when the response is null.
   * @param result - Response from the controller.
   * @return
   */
  private int statusCode(Object result) {
    if (result instanceof ResponseEntity) {
      return ((ResponseEntity) result).getStatusCode().value();
    }
    return HttpStatus.INTERNAL_SERVER_ERROR.value();
  }
}
