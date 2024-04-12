package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorValidatorTest {
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountId";
    private static final String NAME = "directorName";
    private static final LocalDate RESIGNATION_DATE = LocalDate.of(2019, 1, 1);
    private static final LocalDate APPOINTMENT_DATE_AFTER_RES = RESIGNATION_DATE.plusDays(1);

    @Mock
    private DirectorService directorService;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CompanyService companyService;

    private DirectorValidator directorValidator;

    @BeforeEach
    public void setUp() {
        directorValidator = new DirectorValidator(companyService, directorService);
    }
    
    @Test
    @DisplayName("get valid directors - found")
    void getValidDirectorsFound() throws DataException {
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createDirectors()));
        List<String> validNames = directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(validNames.isEmpty());
        assertTrue(validNames.contains(NAME));
    }

    @Test
    @DisplayName("get valid directors - service returns null")
    void getValidDirectorsNullReturned() throws DataException {
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        List<String> validNames = directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(validNames.isEmpty());
    }

    @Test
    @DisplayName("get no valid directors - none found")
    void getValidDirectorsNoneFound() throws DataException {
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createInvalidDirectors()));
        List<String> validNames = directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(validNames.isEmpty());
    }

    @Test
    @DisplayName("get director with app after res date - found")
    void getValidDirectorWithAppAfterRes() throws DataException {
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createDirectorWithAppAfterRes()));
        List<String> validNames = directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(validNames.isEmpty());
        assertTrue(validNames.contains(NAME));
    }

    private Director[] createDirectors() {
        Director[] directors = new Director[1];
        directors[0] = new Director();
        directors[0].setName(NAME);

        return directors;
    }

    private Director[] createInvalidDirectors() {
        Director[] directors = new Director[1];
        directors[0] = new Director();
        directors[0].setName(NAME);
        directors[0].setResignationDate(RESIGNATION_DATE);

        return directors;
    }

    private Director[] createDirectorWithAppAfterRes() {
        Director[] directors = new Director[1];
        directors[0] = new Director();
        directors[0].setName(NAME);
        directors[0].setAppointmentDate(APPOINTMENT_DATE_AFTER_RES);
        directors[0].setResignationDate(RESIGNATION_DATE);

        return directors;
    }
}
