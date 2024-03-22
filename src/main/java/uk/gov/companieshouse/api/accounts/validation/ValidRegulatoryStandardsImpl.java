package uk.gov.companieshouse.api.accounts.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class ValidRegulatoryStandardsImpl implements ConstraintValidator<ValidRegulatoryStandards, String> {

    @Override
    public boolean isValid(String basisOfPreparation, ConstraintValidatorContext context) {

        return StringUtils.isBlank(basisOfPreparation)
                || basisOfPreparation.trim().equalsIgnoreCase("these financial statements have been prepared in accordance with the provisions of financial reporting standard 101")
                || basisOfPreparation.trim().equalsIgnoreCase("these financial statements have been prepared in accordance with the provisions of section 1a (small entities) of financial reporting standard 102");
    }
}
