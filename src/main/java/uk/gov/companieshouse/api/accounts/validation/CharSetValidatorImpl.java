package uk.gov.companieshouse.api.accounts.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.charset.CharSet;
import uk.gov.companieshouse.charset.validation.CharSetValidation;

@Component
public class CharSetValidatorImpl implements ConstraintValidator<CharSetValid, String> {

    @Autowired
    private CharSetValidation charSetValidation;

    private CharSet charSet;

    @Override
    public void initialize(CharSetValid constraintAnnotation) {
        this.charSet = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String underTest, ConstraintValidatorContext constraintValidatorContext) {
        return underTest == null || charSetValidation.validateCharSet(charSet, underTest);
    }
}