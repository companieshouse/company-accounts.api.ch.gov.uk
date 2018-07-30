package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
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

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        when(httpServletRequest.getSession()).thenReturn(httpSessionMock);
        when(httpSessionMock.getAttribute("transaction")).thenReturn(createDummyTransaction(true));
        when(httpServletRequest.getHeader("X-Request-Id")).thenReturn("test");
        when(companyAccountDataEntityMock.getLinks()).thenReturn(createLinksMap());
    }

    @Test
    @DisplayName("Tests the successful creation of an company account resource")
    void canCreateAccount() throws NoSuchAlgorithmException {
        doReturn(companyAccountEntityMock).when(companyAccountTransformer).transform(ArgumentMatchers
                .any(CompanyAccount.class));
        doReturn(companyAccountMock).when(companyAccountTransformer).transform(ArgumentMatchers
                .any(CompanyAccountEntity.class));

        when(companyAccountEntityMock.getData()).thenReturn(companyAccountDataEntityMock);

        CompanyAccount result = companyAccountService.createCompanyAccount(companyAccountMock);
  
        assertNotNull(result);
        assertEquals(companyAccountMock, result);
    }

    /**uk.gov.companieshouse.api.accounts.controller.CompanyAccountControllerTest
     * creates an open or closed dummy transaction depending on the boolean passed into method
     *
     * @param isOpen - true = open, false - closed
     * @return ResponseEntity<> with the desired transaction
     */
    private ResponseEntity<Transaction> createDummyTransaction(boolean isOpen) {
        Transaction transaction = new Transaction();
        transaction.setId("id");
        transaction.setStatus(isOpen ? TransactionStatus.OPEN.getStatus() : TransactionStatus.CLOSED.getStatus());
        transaction.setLinks(createLinksMap());

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    /**
     * creates an a links map with a test self link
     *
     * @return populated links map
     */
    private Map<String, String> createLinksMap() {
        Map<String, String> links = new HashMap<>();
        links.put(LinkType.SELF.getLink(), "selfLinkTest");
        return links;
    }
}