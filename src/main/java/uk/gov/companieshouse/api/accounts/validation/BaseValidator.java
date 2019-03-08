package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;


public class BaseValidator {

    @Value("${incorrect.total}")
    private String incorrectTotal;

    @Value("${date.invalid}")
    protected String dateInvalid;

    @Value("${unexpected.data}")
    protected String unexpectedData;

    @Value("${current.balancesheet.not.equal}")
    protected String currentBalanceSheetNotEqual;

    @Value("${previous.balancesheet.not.equal}")
    protected String previousBalanceSheetNotEqual;

    @Value("${mandatory.element.missing}")
    protected String mandatoryElementMissing;

    @Value("${empty.resource}")
    protected String emptyResource;

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
            if (total != null && ! total.equals(0L)) {
                addError(errors, incorrectTotal, location);
            }
        } else if (total == null || ! total.equals(expectedTotal)) {
            addError(errors, incorrectTotal, location);
        }
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

    /**
     * Add an empty resource error for the given location
     *
     * @param errors
     * @param location
     */
    public Errors addEmptyResourceError(Errors errors, String location) {
        addError(errors, emptyResource, location);

        return errors;
    }
}

