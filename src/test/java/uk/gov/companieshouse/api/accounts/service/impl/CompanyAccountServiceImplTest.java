package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.HashMap;
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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionManager;
import uk.gov.companieshouse.api.accounts.transaction.TransactionStatus;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.WARN)
public class CompanyAccountServiceImplTest {

    @InjectMocks
    private CompanyAccountServiceImpl companyAccountService;

    @Mock
    private CompanyAccount companyAccountMock;

    @Mock
    private CompanyAccountEntity companyAccountEntityMock;

    @Mock
    private CompanyAccountDataEntity companyAccountDataEntityMock;

    @Mock
    private CompanyAccountRepository companyAccountRepository;

    @Mock
    private CompanyAccountTransformer companyAccountTransformer;

    @Mock
    private TransactionManager transactionManagerMock;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpSession httpSessionMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(companyAccountTransformer.transform(Matchers.any(CompanyAccount.class))).thenReturn(companyAccountEntityMock);
        when(companyAccountTransformer.transform(Matchers.any(CompanyAccountEntity.class))).thenReturn(companyAccountMock);


        when(companyAccountEntityMock.getData()).thenReturn(companyAccountDataEntityMock);

        when(httpServletRequest.getHeader("X-Request-Id")).thenReturn("test");

        Map<String, String> links = new HashMap<>();
        links.put(LinkType.SELF.getLink(), "selfLinkTest");
        when(companyAccountDataEntityMock.getLinks()).thenReturn(links);

    }

    @Test
    @DisplayName("Tests the successful creation of an company account resource")
    void canCreateAccount() {
        when(httpServletRequest.getSession()).thenReturn(httpSessionMock);
        when(httpSessionMock.getAttribute("transaction")).thenReturn(createDummyTransaction(true));


        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        CompanyAccount result = companyAccountService.createCompanyAccount(new CompanyAccount());
        assertNotNull(result);
        assertEquals(companyAccountMock, result);

    }

    /**
     * creates an open or closed dummy transaction depending on the boolean passed into method
     *
     * @param isOpen - true = open, false - closed
     * @return ResponseEntity<> with the desired transaction
     */
    private ResponseEntity<Transaction> createDummyTransaction(boolean isOpen) {
        Transaction transaction = new Transaction();

        transaction.setId("id");

        transaction.setStatus(isOpen ? TransactionStatus.OPEN.getStatus() : TransactionStatus.CLOSED.getStatus());

        Map<String, String> links = new HashMap<>();
        links.put(LinkType.SELF.getLink(), "selfLinkTest");
        transaction.setLinks(links);

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
}