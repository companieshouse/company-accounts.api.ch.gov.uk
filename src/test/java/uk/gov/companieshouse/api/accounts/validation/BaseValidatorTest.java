package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseValidatorTest {

    private BaseValidator validator;

    private Errors errors;
    @BeforeEach
    void setup() {
        validator = new BaseValidator();
        validator.incorrectTotal = "incorrect.total";
        validator.emptyResource  = "empty.resource";
        errors = new Errors();
    }

    @Test
    @DisplayName("Test validate the aggregate totals are equal")
    void testValidateAggregateTotalEqual(){
        validator.validateAggregateTotal(1L,1L,"location",errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Test validate the aggregate totals not equal")
    void testValidateAggregateTotalNotEqual(){
        validator.validateAggregateTotal(1L,2L,"location",errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError( validator.incorrectTotal, "location")));

    }

    @Test
    @DisplayName("Test validate the aggregate totals not equal as expected total is null")
    void testValidateAggregateTotalExpectedTotalNull(){
        validator.validateAggregateTotal(1L,null,"location",errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError( validator.incorrectTotal, "location")));
    }

    @Test
    @DisplayName("Test validate the aggregate totals equal with null and 0")
    void testValidateAggregateTotalComparingToNull(){
        validator.validateAggregateTotal(0L,null,"location",errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Test ad empty resource error")
    void testAddEmptyResourceError(){
        errors = validator.addEmptyResourceError(errors,"location");
        assertNotNull(errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(validator.emptyResource,"location")));
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
