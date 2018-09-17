package uk.gov.companieshouse.api.accounts.utility;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.validation.ErrorMessageKeys;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;

import java.util.ArrayList;


@Component
public class BindingResultErrorToErrorMapper {



    /**
     * Maps each binding result error to {@link Error} model, and adds to returned {@link Errors}
     *
     * @param bindingResult
     * @param errors
     * @return
     */
    public Errors mapBindingResultErrorsToErrorModel(BindingResult bindingResult, Errors errors) {

        for (Object object : bindingResult.getAllErrors()) {

            if (object instanceof FieldError) {
                FieldError fieldError = (FieldError) object;

                String field = fieldError.getField();
                String errorMessage = fieldError.getDefaultMessage();

                String location = (field.replaceAll("(.)([A-Z])", "$1_$2")).toLowerCase();

                if ("VALUE_OUTSIDE_RANGE".equals(errorMessage)) {

                    ArrayList arguments = new ArrayList();
                    Object[] argument = fieldError.getArguments();
                    for (Object o : argument) {
                        arguments.add(o);
                    }
                    Error error = new Error(ErrorMessageKeys.VALUE_OUTSIDE_RANGE.getKey(),
                        location,
                        LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
                    error.addErrorValue("lower", argument[2].toString());
                    error.addErrorValue("upper", argument[1].toString());
                    errors.addError(error);

                } else {
                    Error error = new Error(ErrorMessageKeys.valueOf(errorMessage).getKey(),
                        location, LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType());

                    errors.addError(error);
                }
            }
        }

        return errors;
    }
}