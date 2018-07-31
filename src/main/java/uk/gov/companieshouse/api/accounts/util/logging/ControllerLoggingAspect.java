package uk.gov.companieshouse.api.accounts.util.logging;

import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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
  public void logPerformanceStats(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    //Do not change, the target method will not progress without this.
    Object result = joinPoint.proceed();
    long responseTime = System.currentTimeMillis() - startTime;

    AccountsLogger logBuilder = getLogBuilder(joinPoint);
    logBuilder.logStartOfRequestProcessing(String.format("Start Request %s",
        methodName(joinPoint)));

    logBuilder.logEndOfRequestProcessing(String.format("End of Request %s Successfully Completed",
        methodName(joinPoint)), statusCode(result), responseTime);
  }

  private String methodName(JoinPoint joinPoint) {
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

  private AccountsLogger getLogBuilder(JoinPoint joinPoint) {
    HttpServletRequest request = getRequest(joinPoint);
    String accountId = "";
    return new AccountsLoggerImpl(requestId(request), userId(request),
        transactionId(joinPoint), accountId);
  }

  /**
   * Helper to get the HttpServletRequest so that we can get the requestID and the userID.
   */
  private HttpServletRequest getRequest(JoinPoint joinPoint) {
    return (HttpServletRequest) joinPoint.getArgs()[0];
  }

  private String transactionId(JoinPoint joinPoint) {
    return joinPoint.getArgs()[1].toString();
  }

  private int statusCode(Object result) {
    assert(result instanceof ResponseEntity);
    return ((ResponseEntity) result).getStatusCode().value();
  }
}
