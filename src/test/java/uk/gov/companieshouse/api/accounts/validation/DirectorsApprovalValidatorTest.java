package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Secretary;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.impl.SecretaryService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorsApprovalValidatorTest {

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecretaryService secretaryService;

    @Mock
    private DirectorService directorService;

    @InjectMocks
    private DirectorsApprovalValidator validator;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountId";
    private static final String MUST_MATCH_DIRECTOR_OR_SECRETARY = "must_match_director_or_secretary";
    private static final String MUST_MATCH_DIRECTOR_OR_SECRETARY_KEY = "mustMatchDirectorOrSecretary";
    private static final String SECRETARY_NAME = "secretaryName";
    private static final String DIRECTOR_NAME = "directorName";
    private static final String OTHER_NAME = "otherName";


    @Test
    @DisplayName("Validate with a valid approval name ")
    void validateApprovalWithValidName() throws DataException {

        DirectorsApproval directorsApproval = createDirectorsApproval(OTHER_NAME);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a valid secretary approval name ")
    void validateApprovalWhenSecretaryIsNotNullAndNameMatches() throws DataException {

        DirectorsApproval directorsApproval = createDirectorsApproval(SECRETARY_NAME);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createSecretary()));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a invalid secretary approval name ")
    void validateApprovalWhenSecretaryIsNotNullAndNameDoesNotMatch() throws DataException {

       DirectorsApproval directorsApproval = createDirectorsApproval(OTHER_NAME);

        ReflectionTestUtils.setField(validator, MUST_MATCH_DIRECTOR_OR_SECRETARY_KEY, MUST_MATCH_DIRECTOR_OR_SECRETARY);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createSecretary()));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MUST_MATCH_DIRECTOR_OR_SECRETARY, "$.directors_approval.name")));
    }

    @Test
    @DisplayName("Validate with a valid directors approval name ")
    void validateApprovalWhenDirectorIsNotNullAndNameMatches() throws DataException {

        DirectorsApproval directorsApproval = createDirectorsApproval(DIRECTOR_NAME);

        ReflectionTestUtils.setField(validator, MUST_MATCH_DIRECTOR_OR_SECRETARY_KEY, MUST_MATCH_DIRECTOR_OR_SECRETARY);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createDirectors()));

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a invalid directors approval name ")
    void validateApprovalWhenDirectorsIsNotNullAndNameDoesNotMatch() throws DataException {

        DirectorsApproval directorsApproval = createDirectorsApproval(OTHER_NAME);

        ReflectionTestUtils.setField(validator, MUST_MATCH_DIRECTOR_OR_SECRETARY_KEY, MUST_MATCH_DIRECTOR_OR_SECRETARY);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createDirectors()));

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MUST_MATCH_DIRECTOR_OR_SECRETARY, "$.directors_approval.name")));
    }

    @Test
    @DisplayName("Validate with a valid secretary or directors approval name ")
    void validateApprovalWhenSecretaryOrDirectorsIsNotNullAndNameMatches() throws DataException {

        DirectorsApproval directorsApproval = createDirectorsApproval(DIRECTOR_NAME);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createSecretary()));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createDirectors()));

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a invalid directors or secretary approval name ")
    void validateApprovalWhenDirectorsOrSecretaryIsNotNullAndNameDoesNotMatch() throws DataException {

        DirectorsApproval directorsApproval = createDirectorsApproval(OTHER_NAME);

        ReflectionTestUtils.setField(validator, MUST_MATCH_DIRECTOR_OR_SECRETARY_KEY, MUST_MATCH_DIRECTOR_OR_SECRETARY);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createSecretary()));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createDirectors()));

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MUST_MATCH_DIRECTOR_OR_SECRETARY, "$.directors_approval.name")));
    }

    @Test
    @DisplayName("Validate when director is reappointed")
    void validateApprovalWithReappointedDirectors() throws DataException {

        DirectorsApproval directorsApproval = createDirectorsApproval(DIRECTOR_NAME);

        Director[] directors = createReappointedDirector();

        Director newDirector = new Director();
        newDirector.setResignationDate(LocalDate.of(2017, 04, 01));
        newDirector.setAppointmentDate(LocalDate.of(2017, 03, 01));

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createSecretary()));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, directors));

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
        assertNull(directors[2].getResignationDate());
        assertNull(directors[1].getResignationDate());
        assertEquals(newDirector.getResignationDate(), directors[0].getResignationDate());
    }


    private Director[] createDirectors() {

        Director[] directors = new Director[1];
        directors[0] = new Director();
        directors[0].setName(DIRECTOR_NAME);

        return directors;
    }

    private Director[] createReappointedDirector() {

        Director[] directors = new Director[3];

        Director director = createDirectors()[0];

        director.setResignationDate(LocalDate.of(2017, 04, 01));
        director.setAppointmentDate(LocalDate.of(2017, 03, 01));

        directors[0] = director;

        director = new Director();
        director.setName(DIRECTOR_NAME);
        director.setAppointmentDate(LocalDate.of(2017, 01, 01));

        directors[1] = director;

        director = new Director();

        director.setName(OTHER_NAME);
        director.setResignationDate(LocalDate.of(2017, 03, 01));
        director.setAppointmentDate(LocalDate.of(2017, 04, 01));

        directors[2] = director;

        return directors;
    }

    private Secretary createSecretary() {

        Secretary secretary = new Secretary();
        secretary.setName(SECRETARY_NAME);

        return secretary;
    }

    private DirectorsApproval createDirectorsApproval(String name) {

        DirectorsApproval directorsApproval = new DirectorsApproval();
        directorsApproval.setName(name);
        return directorsApproval;
    }

    private Error createError(String error, String path) {

        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}