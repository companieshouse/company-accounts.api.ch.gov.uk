package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees.EmployeesDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees.EmployeesEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.PreviousPeriod;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class EmployeesTransformerTest {
    private static final Long AVERAGE_NUMBER_OF_EMPLOYEES_CURRENT = 11L;
    private static final Long AVERAGE_NUMBER_OF_EMPLOYEES_PREVIOUS = 19L;
    private static final String DETAILS = "details";
    private static final String ETAG = "etag";
    private static final String KIND = "kind";

    private final EmployeesTransformer employeesTransformer = new EmployeesTransformer();

    @Test
    @DisplayName("Tests transformer with empty rest object returns null values ")
    void testTransformerWithEmptyRestObject() {
        EmployeesEntity employeesEntity = employeesTransformer.transform(new Employees());

        assertNotNull(employeesEntity);
        assertNull(employeesEntity.getData().getEtag());
        assertEquals(new HashMap<>(), employeesEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with empty previous period Rest Object")
    void testRestToEntityTransformerWithEmptyPreviousPeriodRestObject() {
        Employees employees = new Employees();

        employees.setEtag(ETAG);
        employees.setKind(KIND);
        employees.setLinks(new HashMap<>());

        CurrentPeriod currentPeriod = createCurrentPeriodRestObject();

        employees.setCurrentPeriod(currentPeriod);

        EmployeesEntity employeesEntity = employeesTransformer.transform(employees);

        assertNotNull(employeesEntity);
        assertNull(employeesEntity.getData().getPreviousPeriodEntity());
        assertFieldsMappedToEntity(employeesEntity);
        assertEquals(new HashMap<>(), employeesEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Rest object and validates values returned")
    void testRestToEntityTransformerWithFullyPopulatedObject() {
        Employees employees = new Employees();

        employees.setEtag(ETAG);
        employees.setKind(KIND);
        employees.setLinks(new HashMap<>());

        employees.setCurrentPeriod(createCurrentPeriodRestObject());
        employees.setPreviousPeriod(createPreviousPeriodRestObject());

        EmployeesEntity employeesEntity = employeesTransformer.transform(employees);

        assertNotNull(employeesEntity);
        assertFieldsMappedToEntity(employeesEntity);
    }

    @Test
    @DisplayName("Tests transformer with empty entity object returns null values ")
    void testTransformerWithEmptyEntityObject() {
        Employees employees = employeesTransformer.transform(new EmployeesEntity());

        assertNotNull(employees);
        assertNull(employees.getEtag());
        assertEquals(new HashMap<>(), employees.getLinks());
    }

    @Test
    @DisplayName("Tests transformer with empty previous period Entity Object")
    void testEntityToRestTransformerWithEmptyPreviousPeriodEntityObject() {
        EmployeesEntity employeesEntity = new EmployeesEntity();
        EmployeesDataEntity employeesDataEntity = new EmployeesDataEntity();

        employeesDataEntity.setEtag(ETAG);
        employeesDataEntity.setKind(KIND);
        employeesDataEntity.setLinks(new HashMap<>());
        employeesDataEntity.setCurrentPeriodEntity(createCurrentPeriodEntityObject());

        employeesEntity.setData(employeesDataEntity);

        Employees employees = employeesTransformer.transform(employeesEntity);

        assertNotNull(employees);
        assertFieldsMappedToRest(employees);
        assertEquals(new HashMap<>(), employees.getLinks());
        assertEquals(ETAG, employees.getEtag());
        assertEquals(KIND, employees.getKind());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Entity object and validates values returned")
    void testEntityToRestTransformerWithFullyPopulatedEntityObject() {
        EmployeesEntity employeesEntity = new EmployeesEntity();
        EmployeesDataEntity employeesDataEntity = new EmployeesDataEntity();

        employeesDataEntity.setEtag(ETAG);
        employeesDataEntity.setKind(KIND);
        employeesDataEntity.setLinks(new HashMap<>());
        employeesDataEntity.setCurrentPeriodEntity(createCurrentPeriodEntityObject());
        employeesDataEntity.setPreviousPeriodEntity(createPreviousPeriodEntityObject());

        employeesEntity.setData(employeesDataEntity);

        Employees employees = employeesTransformer.transform(employeesEntity);

        assertNotNull(employees);
        assertFieldsMappedToRest(employees);
        assertEquals(new HashMap<>(), employees.getLinks());
        assertEquals(ETAG, employees.getEtag());
        assertEquals(KIND, employees.getKind());
    }

    @Test
    @DisplayName("Get accounting note type")
    void getAccountingNoteType() {
        assertEquals(AccountingNoteType.SMALL_FULL_EMPLOYEES, employeesTransformer.getAccountingNoteType());
    }

    private CurrentPeriod createCurrentPeriodRestObject() {
        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setAverageNumberOfEmployees(AVERAGE_NUMBER_OF_EMPLOYEES_CURRENT);
        currentPeriod.setDetails(DETAILS);
        return currentPeriod;
    }


    private PreviousPeriod createPreviousPeriodRestObject() {
        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setAverageNumberOfEmployees(AVERAGE_NUMBER_OF_EMPLOYEES_PREVIOUS);
        return previousPeriod;
    }

    private void assertFieldsMappedToEntity(EmployeesEntity employeesEntity) {
        EmployeesDataEntity employeesDataEntity = employeesEntity.getData();
        
        assertEquals(AVERAGE_NUMBER_OF_EMPLOYEES_CURRENT,
                employeesDataEntity.getCurrentPeriodEntity().getAverageNumberOfEmployees());
        assertEquals(DETAILS, employeesDataEntity.getCurrentPeriodEntity().getDetails());

        assertEquals(new HashMap<>(), employeesDataEntity.getLinks());
        assertEquals(ETAG, employeesDataEntity.getEtag());
        assertEquals(KIND, employeesDataEntity.getKind());

        if (employeesDataEntity.getPreviousPeriodEntity() != null) {
            assertEquals(AVERAGE_NUMBER_OF_EMPLOYEES_PREVIOUS,
                    employeesDataEntity.getPreviousPeriodEntity().getAverageNumberOfEmployees());
        }
    }

    private CurrentPeriodEntity createCurrentPeriodEntityObject() {
        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        currentPeriodEntity.setAverageNumberOfEmployees(AVERAGE_NUMBER_OF_EMPLOYEES_CURRENT);
        currentPeriodEntity.setDetails(DETAILS);
        return currentPeriodEntity;
    }

    private PreviousPeriodEntity createPreviousPeriodEntityObject() {
        PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
        previousPeriodEntity.setAverageNumberOfEmployees(AVERAGE_NUMBER_OF_EMPLOYEES_PREVIOUS);
        return previousPeriodEntity;
    }

    private void assertFieldsMappedToRest(Employees employees) {
        assertEquals(AVERAGE_NUMBER_OF_EMPLOYEES_CURRENT, employees.getCurrentPeriod().getAverageNumberOfEmployees());
        assertEquals(DETAILS, employees.getCurrentPeriod().getDetails());

        assertEquals(new HashMap<>(), employees.getLinks());
        assertEquals(ETAG, employees.getEtag());
        assertEquals(KIND, employees.getKind());

        if (employees.getPreviousPeriod() != null) {
            assertEquals(AVERAGE_NUMBER_OF_EMPLOYEES_PREVIOUS, employees.getPreviousPeriod().getAverageNumberOfEmployees());
        }
    }
}
