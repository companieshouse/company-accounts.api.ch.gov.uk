package uk.gov.companieshouse.api.accounts.validation;

import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;


public class BaseValidator {

    /**
     * Validate the given total is correctly aggregated
     *
     * @param total
     * @param expectedTotal
     * @param location
     * @param errors
     */
    protected void validateAggregateTotal(Long total, Long expectedTotal, String location,
        Errors errors) {
        if (expectedTotal == null) {
            if (total != null && !total.equals(0)) {
                addIncorrectTotalError(errors, location);
            }
        } else if (total == null || !total.equals(expectedTotal)) {
            addIncorrectTotalError(errors, location);
        }
    }

    /**
     * Add an incorrect total error for the given location
     *
     * @param errors
     * @param location
     */
    protected void addIncorrectTotalError(Errors errors, String location) {
        addError(errors, ErrorMessageKeys.INCORRECT_TOTAL, location);
    }

    /**
     * Add an error for the given location
     *
     * @param errors
     * @param messageKey
     * @param location
     */
    protected void addError(Errors errors, ErrorMessageKeys messageKey, String location) {
        errors.addError(new Error(messageKey.getKey(), location, LocationType.JSON_PATH.getValue(),
            ErrorType.VALIDATION.getType()));
    }
}

