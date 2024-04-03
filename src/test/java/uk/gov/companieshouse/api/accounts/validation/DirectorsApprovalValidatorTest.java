package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Secretary;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.SecretaryService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorsApprovalValidatorTest {
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountId";
    private static final String MUST_MATCH_DIRECTOR_OR_SECRETARY = "must_match_director_or_secretary";
    private static final String MUST_MATCH_DIRECTOR_OR_SECRETARY_KEY = "mustMatchDirectorOrSecretary";
    private static final String SECRETARY_NAME = "secretaryName";
    private static final String DIRECTOR_NAME = "directorName";
    private static final String OTHER_NAME = "otherName";

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecretaryService secretaryService;

    @Mock
    private DirectorValidator directorValidator;
    
    @Mock
    private CompanyService companyService;

    private DirectorsApprovalValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new DirectorsApprovalValidator(companyService, secretaryService, directorValidator);
    }
    @Test
    @DisplayName("Validate with a valid approval name ")
    void validateApprovalWithValidName() throws DataException {
        List<String> validNames = new ArrayList<>();
        validNames.add(OTHER_NAME);

        DirectorsApproval directorsApproval = createDirectorsApproval(OTHER_NAME);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));
        when(directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(validNames);

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a valid secretary approval name")
    void validateApprovalWhenSecretaryIsNotNullAndNameMatches() throws DataException {
        List<String> validNames = new ArrayList<>();

        DirectorsApproval directorsApproval = createDirectorsApproval(SECRETARY_NAME);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createSecretary()));
        when(directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(validNames);

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a invalid secretary approval name ")
    void validateApprovalWhenSecretaryIsNotNullAndNameDoesNotMatch() throws DataException {
        List<String> validNames = new ArrayList<>();

        DirectorsApproval directorsApproval = createDirectorsApproval(OTHER_NAME);

        ReflectionTestUtils.setField(validator, MUST_MATCH_DIRECTOR_OR_SECRETARY_KEY, MUST_MATCH_DIRECTOR_OR_SECRETARY);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createSecretary()));
        when(directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(validNames);

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(
                createError()));
    }

    @Test
    @DisplayName("Validate with a valid directors approval name")
    void validateApprovalWhenDirectorIsNotNullAndNameMatches() throws DataException {
        List<String> validNames = new ArrayList<>();
        validNames.add(DIRECTOR_NAME);

        DirectorsApproval directorsApproval = createDirectorsApproval(DIRECTOR_NAME);

        ReflectionTestUtils.setField(validator, MUST_MATCH_DIRECTOR_OR_SECRETARY_KEY, MUST_MATCH_DIRECTOR_OR_SECRETARY);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request)).
                thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));
        when(directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(validNames);

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a invalid directors approval name")
    void validateApprovalWhenDirectorsIsNotNullAndNameDoesNotMatch() throws DataException {
        List<String> validNames = new ArrayList<>();
        validNames.add(DIRECTOR_NAME);

        DirectorsApproval directorsApproval = createDirectorsApproval(OTHER_NAME);

        ReflectionTestUtils.setField(validator, MUST_MATCH_DIRECTOR_OR_SECRETARY_KEY, MUST_MATCH_DIRECTOR_OR_SECRETARY);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));
        when(directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(validNames);

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(
                createError()));
    }

    @Test
    @DisplayName("Validate with a valid secretary or directors approval name")
    void validateApprovalWhenSecretaryOrDirectorsIsNotNullAndNameMatches() throws DataException {
        List<String> validNames = new ArrayList<>();
        validNames.add(DIRECTOR_NAME);

        DirectorsApproval directorsApproval = createDirectorsApproval(DIRECTOR_NAME);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createSecretary()));
        validNames.add(SECRETARY_NAME);

        when(directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(validNames);

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with a invalid directors or secretary approval name ")
    void validateApprovalWhenDirectorsOrSecretaryIsNotNullAndNameDoesNotMatch() throws DataException {
        List<String> validNames = new ArrayList<>();
        validNames.add(DIRECTOR_NAME);

        DirectorsApproval directorsApproval = createDirectorsApproval(OTHER_NAME);

        ReflectionTestUtils.setField(validator, MUST_MATCH_DIRECTOR_OR_SECRETARY_KEY, MUST_MATCH_DIRECTOR_OR_SECRETARY);

        when(secretaryService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createSecretary()));
        validNames.add(SECRETARY_NAME);

        when(directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(validNames);

        Errors errors = validator.validateApproval(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(
                createError()));
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

    private Error createError() {
        return new Error(DirectorsApprovalValidatorTest.MUST_MATCH_DIRECTOR_OR_SECRETARY, "$.directors_approval.name",
                LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
    }
}