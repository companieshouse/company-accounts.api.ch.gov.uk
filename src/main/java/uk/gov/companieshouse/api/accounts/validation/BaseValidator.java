package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;


public class BaseValidator {

    @Value("${incorrect.total}")
    private String incorrectTotal;

    @Value("${date.invalid}")
    protected String dateInvalid;

    @Value("${inconsistent.data}")
    private String inconsistentData;

    @Value("${invalid.note}")
    protected String invalidNote;

    @Value("${current.balancesheet.not.equal}")
    protected String currentBalanceSheetNotEqual;

    @Value("${previous.balancesheet.not.equal}")
    protected String previousBalanceSheetNotEqual;

    @Value("${mandatory.element.missing}")
    private String mandatoryElementMissing;

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
            if (total != null && !total.equals(0L)) {
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
        addError(errors, incorrectTotal, location);
    }

    /**
     * Add an inconsistent data error for the given location
     *
     * @param errors
     * @param location
     */
    protected void addInconsistentDataError(Errors errors, String location) {

        addError(errors, inconsistentData, location);
    }

    /**
     * Add a mandatory element missing error for the given location
     *
     * @param errors
     * @param location
     */
    protected void addMandatoryElementMissingError(Errors errors, String location) {

        addError(errors, mandatoryElementMissing, location);
    }

    /**
     * Add an error for the given location
     *
     * @param errors
     * @param messageKey
     * @param location
     */
    protected void addError(Errors errors, String messageKey, String location) {
        errors.addError(new Error(messageKey, location, LocationType.JSON_PATH.getValue(),
            ErrorType.VALIDATION.getType()));
    }
}

