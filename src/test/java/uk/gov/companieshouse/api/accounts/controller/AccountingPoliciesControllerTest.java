package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPolicies;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class AccountingPoliciesControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String INJECTED_CONTROLLER = "baseController";

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private AccountingPolicies accountingPolicies;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private BaseController<AccountingPolicies> baseController;

    @InjectMocks
    private AccountingPoliciesController accountingPoliciesController;

    @Test
    @DisplayName("Create accountingPolicies resource")
    void createAccountingPoliciesResource() {

        ReflectionTestUtils.setField(accountingPoliciesController, INJECTED_CONTROLLER,
                baseController);

        when(baseController
                .create(accountingPolicies, bindingResult, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseEntity);

        assertEquals(responseEntity,
                accountingPoliciesController
                        .create(accountingPolicies, bindingResult, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Update accountingPolicies resource")
    void updateAccountingPoliciesResource() {

        ReflectionTestUtils.setField(accountingPoliciesController, INJECTED_CONTROLLER,
                baseController);

        when(baseController
                .update(accountingPolicies, bindingResult, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseEntity);

        assertEquals(responseEntity,
                accountingPoliciesController
                        .update(accountingPolicies, bindingResult, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Get accountingPolicies resource")
    void getAccountingPoliciesResource() {

        ReflectionTestUtils.setField(accountingPoliciesController, INJECTED_CONTROLLER,
                baseController);

        when(baseController.get(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseEntity);

        assertEquals(responseEntity,
                accountingPoliciesController.get(COMPANY_ACCOUNTS_ID, request));
    }
}
