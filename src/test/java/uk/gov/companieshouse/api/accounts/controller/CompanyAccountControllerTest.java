package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountControllerTest {

    @Mock
    HttpSession session;
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

    @Mock
    private HttpSession httpSessionMock;

    @Mock
    private Map<String, String> links;
  
    @InjectMocks
    private CompanyAccountController companyAccountController;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {
        when(companyAccountService.save(any(CompanyAccount.class), anyString()))
                .thenReturn(createdCompanyAccount);
        when(request.getSession()).thenReturn(httpSessionMock);
        when(session.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(transaction.getCompanyNumber()).thenReturn("123456");
    }

    @Test
    @DisplayName("Tests the successful creation of an companyAccount resource")
    void canCreateAccount() throws NoSuchAlgorithmException {
        ResponseEntity response = companyAccountController
                .createCompanyAccount(companyAccount, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(new Equals(createdCompanyAccount).matches(response.getBody()));
    }
}