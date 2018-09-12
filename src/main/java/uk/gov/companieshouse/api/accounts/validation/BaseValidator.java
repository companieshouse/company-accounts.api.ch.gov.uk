package uk.gov.companieshouse.api.accounts.validation;

import uk.gov.companieshouse.api.accounts.model.Errors;
import uk.gov.companieshouse.api.accounts.model.NumericRange;


public class BaseValidator {

    /**
     * Validate the optional value is within the given {@link NumericRange}
     *
     * @param value
     * @param range
     * @param errors
     * @param location
     */
    protected void validateOptionalWithinRange(Integer value, NumericRange range, Errors errors,
        String location) {
        if (value != null && !range.inRangeInclusive(value)) {
            addRangeError(errors, location, range);
        }
    }

    /**
     * Validate the given total is correctly aggregated
     *
     * @param total
     * @param expectedTotal
     * @param location
     * @param errors
     */
    protected void validateAggregateTotal(Integer total, Integer expectedTotal, String location,
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
     * Add a range error for the given location
     *
     * @param errors
     * @param location
     * @param range
     */
    protected void addRangeError(Errors errors, String location, NumericRange range) {
        Error error = new Error(ErrorMessageKeys.VALUE_OUTSIDE_RANGE.getKey(), location,
            LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
        error.addErrorValue("lower", Integer.toString(range.getStart()));
        error.addErrorValue("upper", Integer.toString(range.getEnd()));
        errors.addError(error);
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

