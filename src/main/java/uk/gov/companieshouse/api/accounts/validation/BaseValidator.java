package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Value;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;


public class BaseValidator {

    @Value("${incorrect.total}")
    protected String incorrectTotal;

    @Value("${date.invalid}")
    protected String dateInvalid;

    @Value("${date.outside.range}")
    protected String dateOutsideRange;

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

    @Value("${value.required}")
    protected String valueRequired;

    @Value("${mustMatch.directorOrSecretary}")
    protected String mustMatchDirectorOrSecretary;

    @Value("${mustMatch.director}")
    protected String mustMatchDirector;

    @Value("${invalid.value}")
    protected String invalidValue;

    private CompanyService companyService;

    public BaseValidator(CompanyService companyService) {
    	this.companyService = companyService;
    }
    
    /**
     * Validate the given total is correctly aggregated
     *
     * @param total actual total of the number fields
     * @param expectedTotal expected total of the number fields
     * @param location location json path location of the error
     * @param errors errors errors object that holds any errors from submission
     */
    protected void validateAggregateTotal(Long total,
                                          Long expectedTotal,
                                          String location,
                                          Errors errors) {
        if (expectedTotal == null) {
            if (total != null && !total.equals(0L)) {
                addError(errors, incorrectTotal, location);
            }
        } else if (total == null || !total.equals(expectedTotal)) {
            addError(errors, incorrectTotal, location);
        }
    }

    /**
     * Add an error for the given location
     *
     * @param errors errors errors object that holds any errors from submission
     * @param messageKey relevent message key for the given error
     * @param location location json path location of the error
     */
    protected void addError(Errors errors, String messageKey, String location) {
        errors.addError(new Error(messageKey, location, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType()));
    }

    /**
     * Add an empty resource error for the given location
     *
     * @param errors errors object that holds any errors from submission
     * @param location json path location of the error
     */
    public Errors addEmptyResourceError(Errors errors, String location) {
        addError(errors, emptyResource, location);

        return errors;
    }

    protected boolean getIsMultipleYearFiler(Transaction transaction) throws DataException {
        try {
            return companyService.isMultipleYearFiler(transaction);
        } catch (ServiceException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }
}

