package uk.gov.companieshouse.api.accounts.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

@Component
public class ValidRptTransactionTypeImpl implements ConstraintValidator<ValidRptTransactionType, String> {

    private static final Set<String> LEGAL_TRANSACTION_TYPES = new HashSet<>();

    static {
        LEGAL_TRANSACTION_TYPES.add("Money given to the company by a related party");
        LEGAL_TRANSACTION_TYPES.add("Money given to a related party by the company");
    }

    @Override
    public boolean isValid(String rptTransactionType, ConstraintValidatorContext context) {
        return StringUtils.isBlank(rptTransactionType)
                || LEGAL_TRANSACTION_TYPES.contains(rptTransactionType.trim());
    }
}
