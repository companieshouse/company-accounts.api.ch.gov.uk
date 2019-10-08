package uk.gov.companieshouse.api.accounts.service.impl.smallfull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.accounts.model.entity.notes.employees.EmployeesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.service.impl.BaseService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeesServiceTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";

    private static final String EXPECTED_SELF_LINK = TRANSACTION_SELF_LINK + "/company-accounts/" +
                                                        COMPANY_ACCOUNTS_ID + "/small-full/notes/employees";

    private static final String INJECTED_SERVICE = "baseService";

    @Mock
    private Employees employees;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks links;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ResponseObject<Employees> responseObject;

    @Mock
    private BaseService<Employees, EmployeesEntity, SmallFullLinkType> baseService;

    @InjectMocks
    private EmployeesService employeesService;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(employeesService, INJECTED_SERVICE, baseService);
    }

    @Test
    @DisplayName("Create employees resource")
    void createEmployeesResource() throws DataException {

        when(transaction.getLinks()).thenReturn(links);
        when(links.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(baseService
                .create(employees, transaction, COMPANY_ACCOUNTS_ID, request, EXPECTED_SELF_LINK))
                        .thenReturn(responseObject);

        assertEquals(responseObject,
                employeesService
                        .create(employees, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Update employees resource")
    void updateEmployeesResource() throws DataException {

        when(transaction.getLinks()).thenReturn(links);
        when(links.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(baseService
                .update(employees, transaction, COMPANY_ACCOUNTS_ID, request, EXPECTED_SELF_LINK))
                        .thenReturn(responseObject);

        assertEquals(responseObject,
                employeesService
                        .update(employees, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Get employees resource")
    void getEmployeesResource() throws DataException {

        when(baseService.find(COMPANY_ACCOUNTS_ID)).thenReturn(responseObject);

        assertEquals(responseObject,
                employeesService.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete employees resource")
    void deleteEmployeesResource() throws DataException {

        when(baseService.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        assertEquals(responseObject,
                employeesService.delete(COMPANY_ACCOUNTS_ID, request));
    }
}
