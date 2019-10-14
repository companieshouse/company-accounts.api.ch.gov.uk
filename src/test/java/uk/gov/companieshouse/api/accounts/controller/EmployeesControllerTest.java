package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeesControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String INJECTED_CONTROLLER = "baseController";

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Employees employees;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private BaseController<Employees> baseController;

    @InjectMocks
    private EmployeesController employeesController;

    @Test
    @DisplayName("Create employees resource")
    void createEmployeesResource() {

        ReflectionTestUtils.setField(employeesController, INJECTED_CONTROLLER,
                baseController);

        when(baseController
                .create(employees, bindingResult, COMPANY_ACCOUNTS_ID, request))
                        .thenReturn(responseEntity);

        assertEquals(responseEntity,
                employeesController
                        .create(employees, bindingResult, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Update employees resource")
    void updateEmployeesResource() {

        ReflectionTestUtils.setField(employeesController, INJECTED_CONTROLLER,
                baseController);

        when(baseController
                .update(employees, bindingResult, COMPANY_ACCOUNTS_ID, request))
                        .thenReturn(responseEntity);

        assertEquals(responseEntity,
                employeesController
                        .update(employees, bindingResult, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Get employees resource")
    void getEmployeesResource() {

        ReflectionTestUtils.setField(employeesController, INJECTED_CONTROLLER,
                baseController);

        when(baseController.get(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseEntity);

        assertEquals(responseEntity,
                employeesController.get(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete employees resource")
    void deleteEmployeesResource() {

        ReflectionTestUtils.setField(employeesController, INJECTED_CONTROLLER,
                baseController);

        when(baseController.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseEntity);

        assertEquals(responseEntity,
                employeesController.delete(COMPANY_ACCOUNTS_ID, request));
    }
}
