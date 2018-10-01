package uk.gov.companieshouse.api.accounts.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.charset.CharSet;
import uk.gov.companieshouse.charset.validation.CharSetValidation;
import uk.gov.companieshouse.charset.validation.impl.CharSetValidationImpl;

@Component
public class CharSetValidatorImpl implements ConstraintValidator<CharSetValid, String> {


    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && new CharSetValidationImpl()
            .validateCharSet(CharSet.CHARECTER_SET_3, value);
    }
}