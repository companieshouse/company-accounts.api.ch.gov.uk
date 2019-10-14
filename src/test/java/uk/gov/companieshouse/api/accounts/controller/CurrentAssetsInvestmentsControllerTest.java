package uk.gov.companieshouse.api.accounts.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CurrentAssetsInvestments;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentAssetsInvestmentsControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String INJECTED_CONTROLLER = "baseController";

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CurrentAssetsInvestments currentAssetsInvestments;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private BaseController<CurrentAssetsInvestments> baseController;

    @InjectMocks
    private CurrentAssetsInvestmentsController currentAssetsInvestmentsController;

    @Test
    @DisplayName("Create currentAssetsInvestments resource")
    void createCurrentAssetsInvestmentsResource() {

        ReflectionTestUtils.setField(currentAssetsInvestmentsController, INJECTED_CONTROLLER,
                baseController);

        when(baseController
                .create(currentAssetsInvestments, bindingResult, COMPANY_ACCOUNTS_ID, request))
                        .thenReturn(responseEntity);

        assertEquals(responseEntity,
                currentAssetsInvestmentsController
                        .create(currentAssetsInvestments, bindingResult, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Update currentAssetsInvestments resource")
    void updateCurrentAssetsInvestmentsResource() {

        ReflectionTestUtils.setField(currentAssetsInvestmentsController, INJECTED_CONTROLLER,
                baseController);

        when(baseController
                .update(currentAssetsInvestments, bindingResult, COMPANY_ACCOUNTS_ID, request))
                        .thenReturn(responseEntity);

        assertEquals(responseEntity,
                currentAssetsInvestmentsController
                        .update(currentAssetsInvestments, bindingResult, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Get currentAssetsInvestments resource")
    void getCurrentAssetsInvestmentsResource() {

        ReflectionTestUtils.setField(currentAssetsInvestmentsController, INJECTED_CONTROLLER,
                baseController);

        when(baseController.get(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseEntity);

        assertEquals(responseEntity,
                currentAssetsInvestmentsController.get(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete currentAssetsInvestments resource")
    void deleteCurrentAssetsInvestmentsResource() {

        ReflectionTestUtils.setField(currentAssetsInvestmentsController, INJECTED_CONTROLLER,
                baseController);

        when(baseController.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseEntity);

        assertEquals(responseEntity,
                currentAssetsInvestmentsController.delete(COMPANY_ACCOUNTS_ID, request));
    }
}
