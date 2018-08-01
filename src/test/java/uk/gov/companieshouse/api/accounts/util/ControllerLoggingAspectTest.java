package uk.gov.companieshouse.api.accounts.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.controller.CompanyAccountController;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.util.logging.ControllerLoggingAspect;

@ExtendWith(MockitoExtension.class)
public class ControllerLoggingAspectTest {

  private static final String REQUEST_ID = "X-Request-Id";

  private static final String ERIC_IDENTITY = "Eric-Identity";

  private static final String COMPANY_ACCOUNT_ID = "123456";

  private CompanyAccountController proxy;
  private ControllerLoggingAspect constrollerLoggingAspect;

  @Mock
  HttpSession session;
  @Mock
  private HttpServletRequest request;
  @Mock
  private CompanyAccountService companyAccountService;
  @Mock
  private CompanyAccount companyAccount;
  @Mock
  private CompanyAccount createdCompanyAccount;
  @Mock
  private Transaction transaction;
  @Mock
  private ProceedingJoinPoint joinPoint;
  @Mock
  private Signature methodSignature;

  @InjectMocks
  private CompanyAccountController companyAccountController;


  @BeforeEach
  void setUp() {
    AspectJProxyFactory factory = new AspectJProxyFactory(companyAccountController);
    constrollerLoggingAspect = new ControllerLoggingAspect();
    factory.addAspect(constrollerLoggingAspect);
    proxy = factory.getProxy();
  }

  @Test
  public void logTraceAndStats() throws Throwable {
    Object[] methodParameters = {request, "transactionId", companyAccount};
    when(joinPoint.getArgs()).thenReturn(methodParameters);
    when(joinPoint.getSignature()).thenReturn(methodSignature);

    constrollerLoggingAspect.logTraceAndStats(joinPoint);

    verify(joinPoint, times(1)).proceed();
    //the signature is called 2 times, once
    //to get the method name and then to get the declared type.
    verify(joinPoint, times(2)).getSignature();
    //verify the declaringType is called.
    verify(methodSignature, times(1)).getDeclaringTypeName();
    //verify the X-Request-Id is fetched
    verify(request, times(1)).getHeader(REQUEST_ID);
    //verify the Eric-Identity is fetched
    verify(request, times(1)).getHeader(ERIC_IDENTITY);
  }

  @Test
  public void controlledAspectIsTriggered() throws Throwable {
    when(companyAccountService.save(any(CompanyAccount.class), anyString()))
        .thenReturn(createdCompanyAccount);
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
    when(transaction.getCompanyNumber()).thenReturn(COMPANY_ACCOUNT_ID);
    proxy.createCompanyAccount(request, "transaction_id", companyAccount);
    verify(companyAccountService, times(1)).save(companyAccount, COMPANY_ACCOUNT_ID);
  }


  @AfterEach
  void tearDown() {
    constrollerLoggingAspect = null;
    proxy = null;
    companyAccountController = null;
  }
}
