package uk.gov.companieshouse.api.accounts.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;

@Component
public class ErrorMapper {

    @Autowired
    private Environment environment;

    /**
     * Maps each binding result error to {@link Error} model, and adds to returned {@link Errors}
     */
    public Errors mapBindingResultErrorsToErrorModel(BindingResult bindingResult) {
        Errors errors = new Errors();

        for (ObjectError object : bindingResult.getAllErrors()) {

            if (object instanceof FieldError fieldError) {
                addFieldError(fieldError, errors, fieldError.getObjectName());
            }
        }

        return errors;
    }

    /**
     * Maps each binding result error to {@link Error} model, and adds to returned {@link Errors}
     */
    public Errors mapBindingResultErrorsToErrorModel(BindingResult bindingResult, String beanType) {
        Errors errors = new Errors();

        for (ObjectError object : bindingResult.getAllErrors()) {

            if (object instanceof FieldError fieldError) {
                addFieldError(fieldError, errors, beanType);
            }
        }

        return errors;
    }

    private void addFieldError(FieldError fieldError, Errors errors, String objectName) {
        String field = fieldError.getField();
        String errorMessage = fieldError.getDefaultMessage();

        String location =
                "$." + ((objectName + "." + field).replaceAll("(.)([A-Z])", "$1_$2")).toLowerCase();

        if ("value.outside.range".equals(errorMessage) || "invalid.input.length"
                .equals(errorMessage)) {

            Object[] argument = fieldError.getArguments();

            Error error = new Error(
                    environment.resolvePlaceholders("${" + errorMessage + "}"),
                    location,
                    LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());

            if (argument != null && argument.length > 1) {
                error.addErrorValue("lower", argument[2].toString());
                error.addErrorValue("upper", argument[1].toString());
            }

            errors.addError(error);
        } else if ("max.length.exceeded".equals(errorMessage)) {
            Object[] argument = fieldError.getArguments();

            Error error = new Error(
                    environment.resolvePlaceholders("${" + errorMessage + "}"),
                    location,
                    LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
            if (argument != null && argument.length > 0) {
                error.addErrorValue("max_length", argument[1].toString());
            }

            errors.addError(error);

        } else {
            Error error = new Error(errorMessage,
                    location, LocationType.JSON_PATH.getValue(),
                    ErrorType.VALIDATION.getType());

            errors.addError(error);
        }
    }
}