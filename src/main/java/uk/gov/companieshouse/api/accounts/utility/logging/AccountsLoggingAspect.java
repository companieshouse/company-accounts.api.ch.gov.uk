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
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Aspect
@Component
public class AccountsLoggingAspect {
  private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

  @Pointcut("execution(* uk.gov.companieshouse.api.accounts.controller.*.*(..))")
  public void controllerMethods(){

  }


  @Pointcut("execution(* uk.gov.companieshouse.api.accounts.service.*.*(..))")
  public void serviceMethods(){

  }


  @Before("controllerMethods() || serviceMethods()")
  public void logControllerMethodTrace(JoinPoint joinPoint){
      LOG.info(String.format("Entering into %s",
          getMethodName(joinPoint)));
  }

  @AfterReturning(pointcut = "controllerMethods() || serviceMethods()", returning = "retVal")
  public void logControllerAfter(JoinPoint joinPoint, Object retVal){
    LOG.debug(String.format("Returned : %s from %s", retVal, getMethodName(joinPoint)));
  }

  @Around("controllerMethods() || serviceMethods()")
  public void logControllerPerformanceStats(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    LOG.debug(String.format("The method %s Started At %s",
        getMethodName(joinPoint), startTime));
    joinPoint.proceed();

    long timeTaken = System.currentTimeMillis() - startTime;

    LOG.debug(String.format("%s %nCompleted in: %s milliseconds",
        getMethodName(joinPoint), timeTaken));
  }

  @AfterThrowing(pointcut = "controllerMethods() || serviceMethods()", throwing = "exception")
  public void logControllerException(JoinPoint joinPoint, Exception exception){
    if (exception instanceof RuntimeException ) {
      LOG.error(String.format("%s caused exception %s", getMethodName(joinPoint), exception));
    }
  }

  private String getMethodName(JoinPoint joinPoint) {
    return String.format("%s.%s",
        joinPoint.getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName());
  }
}
