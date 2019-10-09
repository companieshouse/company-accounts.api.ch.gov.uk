package uk.gov.companieshouse.api.accounts.service.impl.smallfull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPolicies;
import uk.gov.companieshouse.api.accounts.service.impl.BaseService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountingPoliciesServiceTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";

    private static final String EXPECTED_SELF_LINK = TRANSACTION_SELF_LINK + "/company-accounts/" +
            COMPANY_ACCOUNTS_ID + "/small-full/notes/accounting-policy";

    private static final String INJECTED_SERVICE = "baseService";

    @Mock
    private AccountingPolicies accountingPolicies;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks links;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ResponseObject<AccountingPolicies> responseObject;

    @Mock
    private BaseService<AccountingPolicies, AccountingPoliciesEntity, SmallFullLinkType> baseService;

    @InjectMocks
    private AccountingPoliciesService accountingPoliciesService;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(accountingPoliciesService, INJECTED_SERVICE, baseService);
    }

    @Test
    @DisplayName("Create accountingPolicies resource")
    void createAccountingPoliciesResource() throws DataException {

        when(transaction.getLinks()).thenReturn(links);
        when(links.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(baseService
                .create(accountingPolicies, transaction, COMPANY_ACCOUNTS_ID, request, EXPECTED_SELF_LINK))
                .thenReturn(responseObject);

        assertEquals(responseObject,
                accountingPoliciesService
                        .create(accountingPolicies, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Update accountingPolicies resource")
    void updateAccountingPoliciesResource() throws DataException {

        when(transaction.getLinks()).thenReturn(links);
        when(links.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(baseService
                .update(accountingPolicies, transaction, COMPANY_ACCOUNTS_ID, request, EXPECTED_SELF_LINK))
                .thenReturn(responseObject);

        assertEquals(responseObject,
                accountingPoliciesService
                        .update(accountingPolicies, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Get accountingPolicies resource")
    void getAccountingPoliciesResource() throws DataException {

        when(baseService.find(COMPANY_ACCOUNTS_ID)).thenReturn(responseObject);

        assertEquals(responseObject,
                accountingPoliciesService.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete accountingPolicies resource")
    void deleteAccountingPoliciesResource() throws DataException {

        when(baseService.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        assertEquals(responseObject,
                accountingPoliciesService.delete(COMPANY_ACCOUNTS_ID, request));
    }
}
