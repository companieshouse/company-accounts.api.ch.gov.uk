package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

<<<<<<<HEAD
    =======
import java.security.NoSuchAlgorithmException;
>>>>>>>origin/develop
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Equals;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;


@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountControllerTest {

  @Mock
  private HttpServletRequest mockRequest;

  @Mock
  private HttpServletRequest request;
  @Mock
  private Transaction transaction;
  @Mock
  private CompanyAccount companyAccount;
  @Mock
  private CompanyAccount createdCompanyAccount;
  @Mock
  private CompanyAccountService companyAccountService;
  @InjectMocks
  private CompanyAccountController companyAccountController;

  @BeforeEach
  public void setUp() throws NoSuchAlgorithmException {
    when(companyAccountService.save(any(CompanyAccount.class), anyString()))
        .thenReturn(createdCompanyAccount);
    when(request.getAttribute(anyString())).thenReturn(transaction);
    when(transaction.getCompanyNumber()).thenReturn("123456");
  }

  @Test
  @DisplayName("Tests the successful creation of an companyAccount resource")
  public void canCreateAccount() throws NoSuchAlgorithmException {
    ResponseEntity response = companyAccountController
        .createCompanyAccount(request, "transaction_id", companyAccount);
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(new Equals(createdCompanyAccount).matches(response.getBody()));
  }
}