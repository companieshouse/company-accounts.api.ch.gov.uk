package uk.gov.companieshouse.api.accounts.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;

@Component
public class ErrorMapper {

  @Value("${value.outside.range}")
  private String valueOutsideRange;

  @Value("${max.length.exceeded}")
  private String maxLengthExceeded;

    /**
     * Maps each binding result error to {@link Error} model, and adds to returned {@link Errors}
     *
     * @param bindingResult
     * @return
     */
    public Errors mapBindingResultErrorsToErrorModel(BindingResult bindingResult) {

        Errors errors = new Errors();

        for (Object object : bindingResult.getAllErrors()) {

            if (object instanceof FieldError) {
                FieldError fieldError = (FieldError) object;

                String field = fieldError.getField();
                String errorMessage = fieldError.getDefaultMessage();

                //Convert returned location to a json path
                String period = fieldError.getObjectName() + ".";
                String location = "$." + ((period + field).replaceAll("(.)([A-Z])", "$1_$2")).toLowerCase();

                if ("value.outside.range".equals(errorMessage)) {

                    Object[] argument = fieldError.getArguments();

                    Error error = new Error(valueOutsideRange,
                        location,
                        LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
                    error.addErrorValue("lower", argument[2].toString());
                    error.addErrorValue("upper", argument[1].toString());
                    errors.addError(error);

                } else if ("max.length.exceeded".equals(errorMessage)) {

                    Object[] argument = fieldError.getArguments();

                    Error error = new Error(maxLengthExceeded,
                            location,
                            LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
                    error.addErrorValue("max_length", argument[1].toString());
                    errors.addError(error);

                } else {
                    Error error = new Error(errorMessage,
                        location, LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType());

                    errors.addError(error);
                }
            }
        }

        return errors;
    }
}