package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class ValidRegulatoryStandardsImpl implements ConstraintValidator<ValidRegulatoryStandards, String> {

    @Override
    public boolean isValid(String basisOfOperation, ConstraintValidatorContext context) {

        return basisOfOperation.trim().toLowerCase().equals("these financial statements have been prepared in accordance with the provisions of financial reporting standard 101")
                || basisOfOperation.trim().toLowerCase().equals("these financial statements have been prepared in accordance with the provisions of section 1a (small entities) of financial reporting standard 102");
    }
}
