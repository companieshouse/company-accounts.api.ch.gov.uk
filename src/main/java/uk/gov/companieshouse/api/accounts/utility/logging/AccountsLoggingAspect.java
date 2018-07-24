package uk.gov.companieshouse.api.accounts.utility.logging;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

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
  public void logControllerMethodTrace(JoinPoint joinPoint) {
    if (apiLogging.isMethodTraceEnabled()) {
      LOG.debug(String.format("Entering into %s with arguments :",
          methodName(joinPoint), joinPoint.getArgs()));
    }
  }

  @AfterReturning(pointcut = "serviceMethods()", returning = "retVal")
  public void logControllerAfter(JoinPoint joinPoint, Object retVal) {
    if (apiLogging.isMethodTraceEnabled()) {
      LOG.debug(
          String.format("Exited : %s with return value %s", methodName(joinPoint), retVal));
    }
  }

  @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
  public void logControllerException(JoinPoint joinPoint, Exception exception) {
    if (exception instanceof RuntimeException) {
      LOG.error(String.format("%s caused exception %s", methodName(joinPoint), exception));
    }
  }

  @Around("controllerMethods()")
  public void logControllerPerformanceStats(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();

    //Do not change, the target method will not progress without this.
    joinPoint.proceed();

    long timeTaken = System.currentTimeMillis() - startTime;

    if (apiLogging.isPerformanceStatsEnabled()) {
      LOG.debug(String.format("Controller %s called At %s",
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
}
