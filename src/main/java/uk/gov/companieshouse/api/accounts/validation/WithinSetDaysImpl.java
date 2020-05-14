package uk.gov.companieshouse.api.accounts.validation;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

@Component
public class WithinSetDaysImpl implements ConstraintValidator<WithinSetDays, LocalDate> {

    private int numOfDays;
    private boolean allowNulls;
    
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (allowNulls && date == null) {
            return true;
        } else if (!allowNulls && date == null) {
        	return false;
        }

        LocalDate today = LocalDate.now();
        
        return date.isBefore(today.plusDays(numOfDays)) && date.isAfter(today.minusDays(numOfDays));

    }

    @Override
    public void initialize(WithinSetDays constraintAnnotation) {
    	this.numOfDays = constraintAnnotation.numOfDays();
    	this.allowNulls = constraintAnnotation.allowNulls();
    }
}
