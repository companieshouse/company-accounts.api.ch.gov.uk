package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CicReportValidatorTest {

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyService companyService;

    private CicReportValidator validator;

    private static final String CIC_REPORT_PATH = "$.cic_report";

    private static final String UNEXPECTED_DATA_NAME = "unexpectedData";
    private static final String UNEXPECTED_DATA_VALUE = "unexpected.data";

    @BeforeEach
    private void setUp() {
        validator = new CicReportValidator(companyService);
    }
    
    @Test
    @DisplayName("Validate cic report creation - CIC company")
    void validateCicReportCreationForCICCompany() throws ServiceException, DataException {

        when(companyService.isCIC(transaction)).thenReturn(true);

        Errors errors = validator.validateCicReportCreation(transaction);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate cic report creation - non CIC company")
    void validateCicReportCreationForNonCICCompany() throws ServiceException, DataException {

        when(companyService.isCIC(transaction)).thenReturn(false);
        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME, UNEXPECTED_DATA_VALUE);

        Errors errors = validator.validateCicReportCreation(transaction);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE, CIC_REPORT_PATH)));
    }

    @Test
    @DisplayName("Validate cic report creation - service throws exception")
    void validateCicReportCreationServiceException() throws ServiceException {

        when(companyService.isCIC(transaction)).thenThrow(ServiceException.class);

        assertThrows(DataException.class, () -> validator.validateCicReportCreation(transaction));
    }

    private Error createError(String error, String path) {

        return new Error(error, path, LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
    }
}
