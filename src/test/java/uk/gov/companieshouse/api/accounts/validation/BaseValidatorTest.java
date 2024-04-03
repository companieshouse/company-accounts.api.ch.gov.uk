package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseValidatorTest {
    @Mock
    private CompanyService mockCompanyService;

    private BaseValidator validator;

    private Errors errors;

    @BeforeEach
    void setup() {
        validator = new BaseValidator(mockCompanyService);
        validator.incorrectTotal = "incorrect.total";
        validator.emptyResource = "empty.resource";
        errors = new Errors();
    }

    @Test
    @DisplayName("Test validate the aggregate totals are equal")
    void testValidateAggregateTotalEqual() {
        validator.validateAggregateTotal(1L, 1L, "location", errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Test validate the aggregate totals not equal")
    void testValidateAggregateTotalNotEqual() {
        validator.validateAggregateTotal(1L, 2L, "location", errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(validator.incorrectTotal)));
    }

    @Test
    @DisplayName("Test validate the aggregate totals not equal as expected total is null")
    void testValidateAggregateTotalExpectedTotalNull() {
        validator.validateAggregateTotal(1L, null, "location", errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(validator.incorrectTotal)));
    }

    @Test
    @DisplayName("Test validate the aggregate totals equal with null and 0")
    void testValidateAggregateTotalComparingToNull() {
        validator.validateAggregateTotal(0L, null, "location", errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Test ad empty resource error")
    void testAddEmptyResourceError() {
        errors = validator.addEmptyResourceError(errors, "location");
        assertNotNull(errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(validator.emptyResource)));
    }

    private Error createError(String error) {
        return new Error(error, "location", LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
    }
}
