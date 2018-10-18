package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;


public class BaseValidator {

    @Value("${incorrect.total}")
    private String incorrectTotal;

    @Value("${date.invalid}")
    protected String dateInvalid;

    /**
     * Validate the given total is correctly aggregated
     */
    protected void validateAggregateTotal(Long total, Long expectedTotal, String location,
        Errors errors) {
        if (expectedTotal == null) {
            if (total != null && !total.equals(0L)) {
                addIncorrectTotalError(errors, location);
            }
        } else if (total == null || !total.equals(expectedTotal)) {
            addIncorrectTotalError(errors, location);
        }
    }

    /**
     * Add an incorrect total error for the given location
     */
    protected void addIncorrectTotalError(Errors errors, String location) {
        addError(errors, incorrectTotal, location);
    }

    /**
     * Add an error for the given location
     */
    protected void addError(Errors errors, String messageKey, String location) {
        errors.addError(new Error(messageKey, location, LocationType.JSON_PATH.getValue(),
            ErrorType.VALIDATION.getType()));
    }

}

