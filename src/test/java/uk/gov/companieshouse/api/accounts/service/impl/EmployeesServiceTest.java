package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.employees.EmployeesDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.employees.EmployeesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.repository.EmployeesRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.EmployeesTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeesServiceTest {

    @InjectMocks
    private EmployeesService mockEmployeesService;

    @Mock
    private EmployeesTransformer mockTransformer;

    @Mock
    private Employees mockEmployees;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private EmployeesRepository mockRepository;

    @Mock
    private DuplicateKeyException mockDuplicateKeyException;

    @Mock
    private KeyIdGenerator mockKeyIdGenerator;

    @Mock
    private MongoException mockMongoException;

    @Mock
    private SmallFullService mockSmallFullService;

    private EmployeesEntity employeesEntity;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String EMPLOYEES_ID = "employeesId";


    @BeforeAll
    void setUp() {

        EmployeesDataEntity dataEntity = new EmployeesDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), "self_link");
        dataEntity.setLinks(links);

        employeesEntity = new EmployeesEntity();
        employeesEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of an employees resource")
    void canCreateEmployees() throws DataException {

        when(mockTransformer.transform(mockEmployees)).thenReturn(employeesEntity);

        ResponseObject<Employees> result =
                mockEmployeesService.create(mockEmployees, mockTransaction,
                "", mockRequest);

        assertNotNull(result);
        assertEquals(ResponseStatus.CREATED, result.getStatus());
        assertEquals(mockEmployees, result.getData());
        verify(mockSmallFullService).addLink(anyString(), any(SmallFullLinkType.class),
                anyString(), any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("Tests the duplicate key when creating an employees resource")
    void createEmployeesDuplicateKey() throws DataException {

        doReturn(employeesEntity).when(mockTransformer).transform(ArgumentMatchers
                .any(Employees.class));
        when(mockRepository.insert(employeesEntity)).thenThrow(mockDuplicateKeyException);

        ResponseObject<Employees> result =
                mockEmployeesService.create(mockEmployees,
                        mockTransaction, "", mockRequest);

        assertNotNull(result);
        assertEquals(result.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests for a mongo exception when creating employees")
    void createEmployeesMongoExceptionFailure() throws Exception {

        when(mockTransformer.transform(any(Employees.class))).thenReturn(employeesEntity);

        when(mockRepository.insert(employeesEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
                () -> mockEmployeesService.create(mockEmployees,
                        mockTransaction, "", mockRequest));
                        
    }

    @Test
    @DisplayName("Test the successful delete of an employees resource")
    void deleteEmployees() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.EMPLOYEES.getName()))
                .thenReturn(EMPLOYEES_ID);
        when(mockRepository.existsById(EMPLOYEES_ID)).thenReturn(true);
        doNothing().when(mockRepository).deleteById(EMPLOYEES_ID);

        ResponseObject<Employees> responseObject = mockEmployeesService.
                delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.UPDATED);
        verify(mockSmallFullService, Mockito.times(1))
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.EMPLOYEES_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Test an attempt to delete an empty resource produces a not found response")
    void deleteEmptyEmployees() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.EMPLOYEES.getName()))
                .thenReturn(EMPLOYEES_ID);
        when(mockRepository.existsById(EMPLOYEES_ID)).thenReturn(false);
        ResponseObject<Employees> responseObject = mockEmployeesService.
                delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.NOT_FOUND);
        verify(mockSmallFullService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.EMPLOYEES_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Tests mongo exception thrown on deletion of an employees resource")
    void deleteEmployeesMongoException() {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.EMPLOYEES.getName()))
                .thenReturn(EMPLOYEES_ID);
        when(mockRepository.existsById(EMPLOYEES_ID)).thenReturn(true);
        doThrow(mockMongoException).when(mockRepository).deleteById(EMPLOYEES_ID);

        assertThrows(DataException.class, () -> mockEmployeesService.
                delete(COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Tests the successful update of an employees resource")
    void canUpdateEmployees() throws DataException {

        when(mockTransformer.transform(mockEmployees)).thenReturn(employeesEntity);

        ResponseObject<Employees> result = mockEmployeesService.update(mockEmployees, mockTransaction,
                "", mockRequest);

        assertNotNull(result);
        assertEquals(mockEmployees, result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating an employees resource")
    void updateEmployeesMongoExceptionFailure() throws DataException {

        doReturn(employeesEntity).when(mockTransformer).transform(ArgumentMatchers
                .any(Employees.class));
        when(mockRepository.save(employeesEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
                () -> mockEmployeesService.update(mockEmployees, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful find of an employees resource")
    void findEmployees() throws DataException {

        when(mockRepository.findById(""))
                .thenReturn(Optional.ofNullable(employeesEntity));
        when(mockTransformer.transform(employeesEntity)).thenReturn(mockEmployees);

        ResponseObject<Employees> result = mockEmployeesService.findById("", mockRequest);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Tests when employees resource not found")
    void findEmployeesResponseNotFound() throws DataException {

        when(mockRepository.findById(""))
                .thenReturn(Optional.ofNullable(null));

        ResponseObject<Employees> result = mockEmployeesService.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(responseStatusNotFound(), result.getStatus());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of an employees resource")
    void findEmployeesMongoException() {

        when(mockRepository.findById("")).thenThrow(mockMongoException);
        assertThrows(DataException.class, () -> mockEmployeesService.findById("", mockRequest));
    }

    private ResponseStatus responseStatusNotFound() {
        ResponseObject<RestObject> responseObject = new ResponseObject<>(ResponseStatus.NOT_FOUND);
        return responseObject.getStatus();
    }
}
