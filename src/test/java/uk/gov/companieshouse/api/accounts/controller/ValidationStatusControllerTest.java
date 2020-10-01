package uk.gov.companieshouse.api.accounts.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.model.validation.ValidationStatus;
import uk.gov.companieshouse.api.accounts.service.ValidationStatusService;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ValidationStatusControllerTest {

    @Mock
    ValidationStatusService service;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    ValidationStatusController controller;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @Test
    @DisplayName("Get - validation status with no errors | is valid")
    void getValidationStatusNoErrorsIsValid() throws DataException {

        Errors errors = new Errors(); //Empty, no errors.

        when(service.getValidationErrors(COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        ResponseEntity<ValidationStatus> responseEntity = controller.getValidationStatus(COMPANY_ACCOUNTS_ID, request);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertTrue(responseEntity.getBody().getIsValid());
    }

    @Test
    @DisplayName("Get - Throws Data exception")
    void getValidationStatusThrowsException() throws DataException {

        when(service.getValidationErrors(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity<ValidationStatus> responseEntity = controller.getValidationStatus(COMPANY_ACCOUNTS_ID, request);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
