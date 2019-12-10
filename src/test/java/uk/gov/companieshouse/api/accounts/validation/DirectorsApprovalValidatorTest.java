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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorsApprovalValidatorTest {

    private DirectorsApproval directorsApproval;

    private Errors errors;

    private Secretary secretary;

    private Director[] directors;

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
    private static final String VALUE_REQUIRED = "value_required";
    private static final String VALUE_REQUIRED_KEY = "valueRequired";
    private static final String NAME = "name";
    private static final String INVALID_NAME = "invalidName";

    @Test
    @DisplayName("Validate with a valid approval name ")
    void validateApprovalWithValidName() throws DataException {

        directorsApproval = new DirectorsApproval();
        directorsApproval.setName(NAME);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a valid secretary approval name ")
    void validateApprovalWhenSecretaryIsNotNullAndNameMatches() throws DataException {

        directorsApproval = new DirectorsApproval();
        directorsApproval.setName(NAME);

        Secretary secretary = new Secretary();
        secretary.setName(NAME);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, secretary));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a invalid secretary approval name ")
    void validateApprovalWhenSecretaryIsNotNullAndNameDoesNotMatch() throws DataException {

        createDirectorsAndSecretary(INVALID_NAME);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, secretary));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, directors));

        errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.directors_report.approval.name")));
    }

    @Test
    @DisplayName("Validate with a valid directors approval name ")
    void validateApprovalWhenDirectorIsNotNullAndNameMatches() throws DataException {

        createDirectorsAndSecretary(NAME);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, secretary));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a invalid directors approval name ")
    void validateApprovalWhenDirectorsIsNotNullAndNameDoesNotMatch() throws DataException {

        createDirectorsAndSecretary(INVALID_NAME);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, secretary));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, directors));

        errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.directors_report.approval.name")));
    }

    @Test
    @DisplayName("Validate with a valid secretary or directors approval name ")
    void validateApprovalWhenSecretaryOrDirectorsIsNotNullAndNameMatches() throws DataException {

        createDirectorsAndSecretary(NAME);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, secretary));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a invalid directors or secretary approval name ")
    void validateApprovalWhenDirectorsOrSecretaryIsNotNullAndNameDoesNotMatch() throws DataException {

        createDirectorsAndSecretary(INVALID_NAME);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, secretary));
        when(directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, directors));

        errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.directors_report.approval.name")));
    }

    private void createDirectorsAndSecretary(String name) {

        directorsApproval = new DirectorsApproval();
        directorsApproval.setName(NAME);
        errors = new Errors();
        directors = new Director[1];
        directors[0] = new Director();
        directors[0].setName(name);
        secretary = new Secretary();
        secretary.setName(name);
    }

    private Error createError(String error, String path) {

        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}