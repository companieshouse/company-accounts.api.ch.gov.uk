package uk.gov.companieshouse.api.accounts.validation;

import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidRptTransactionTypeImpl implements ConstraintValidator<ValidRptTransactionType, String> {

    @Override
    public boolean isValid(String rptTransactionType, ConstraintValidatorContext context) {
        return StringUtils.isBlank(rptTransactionType)
                || rptTransactionType.trim().equalsIgnoreCase("money given to the company by a related party")
                || rptTransactionType.trim().equalsIgnoreCase( "money given to a related party by the company");
    }
}
