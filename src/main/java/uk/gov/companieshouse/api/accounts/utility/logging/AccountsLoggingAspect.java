package uk.gov.companieshouse.api.accounts.utility.logging;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Aspect
@Component
public class AccountsLoggingAspect {

  private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

  @Autowired
  private ApiLogging apiLogging;


  @Pointcut("execution(* uk.gov.companieshouse.api.accounts.controller.*.*(..))")
  private void controllerMethods() {
    //Defines the point cut for controller methods, the method body kept empty on purpose.
  }

  @Pointcut("execution(* uk.gov.companieshouse.api.accounts.service.*.*(..))")
  private void serviceMethods() {
    //Defines the point cut for controller methods, the method body kept empty on purpose.
  }


  @Before("serviceMethods()")
  public void logServiceMethodTrace(JoinPoint joinPoint) {
    if (apiLogging.isMethodTraceEnabled()) {
      String logMessage = String
          .format("Entering into %s with arguments :%s", methodName(joinPoint),
              methodParameters(joinPoint));
      LOG.debug(logMessage);
    }
  }

  @AfterReturning(pointcut = "serviceMethods()", returning = "retVal")
  public void logServiceMethodAfterTrace(JoinPoint joinPoint, Object retVal) {
    if (apiLogging.isMethodTraceEnabled()) {
      LOG.debug(
          String.format("Exited : %s with return value: %s", methodName(joinPoint), retVal));
    }
  }

  @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
  public void logServiceException(JoinPoint joinPoint, Exception exception) {
    if (exception instanceof RuntimeException) {
      LOG.error(String.format("%s Error occurred : %s", methodName(joinPoint), exception.getMessage()));
    }
  }

  @Around("controllerMethods()")
  public void logPerformanceStats(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();

    //Do not change, the target method will not progress without this.
    joinPoint.proceed();

    long timeTaken = System.currentTimeMillis() - startTime;

    if (apiLogging.isPerformanceStatsEnabled()) {
      LOG.debug(String.format("%s called At %s",
          methodName(joinPoint), startTime));
      LOG.debug(String.format("%s Completed in: %s milliseconds",
          methodName(joinPoint), timeTaken));
    }
  }

  private String methodName(JoinPoint joinPoint) {
    return String.format("%s.%s",
        joinPoint.getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName());
  }

  private Map<String, String> methodParameters(JoinPoint joinPoint) {
    Map<String, String> parameterMap = new HashMap<>();
    Arrays.stream(joinPoint.getArgs())
        .forEach(
            objecct -> parameterMap.put(objecct.getClass().getName(), objecct.toString())
        );
    return parameterMap;
  }
}
