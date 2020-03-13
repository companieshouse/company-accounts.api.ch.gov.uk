package uk.gov.companieshouse.api.accounts.utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ErrorMapperTest {

    @InjectMocks
    private ErrorMapper errorMapper;

    @Mock
    private Environment environment;

    @Mock
    private BindingResult mockBindingResult;

    @Test
    @DisplayName("Test Mapping Error to Error Model correctly")
    void testMapErrorToErrorModel() {
        when(mockBindingResult.getAllErrors()).thenReturn(getAllErrors());
        when(environment.resolvePlaceholders(any())).thenReturn("error_string");
        Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult);
        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        assertTrue(errors.containsError(createRangeError("error_string", "$.object1.field1","0","9999")));
        assertTrue(errors.containsError(createRangeError("error_string", "$.object2.field2","0","9999")));
        assertTrue(errors.containsError(createMaxError("error_string", "$.object3.field3","0")));
        assertTrue(errors.containsError(createError("incorrect.total", "$.object4.field4")));
    }

    @Test
    @DisplayName("Test Mapping Error to Error Model with object name correctly")
    void testMapErrorToErrorModelWithObjectName() {
        when(mockBindingResult.getAllErrors()).thenReturn(getAllErrors());
        when(environment.resolvePlaceholders(any())).thenReturn("error_string");
        Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult, "objectInError");
        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        assertTrue(errors.containsError(createRangeError("error_string", "$.object_in_error.field1","0","9999")));
        assertTrue(errors.containsError(createRangeError("error_string", "$.object_in_error.field2","0","9999")));
        assertTrue(errors.containsError(createMaxError("error_string", "$.object_in_error.field3","0")));
        assertTrue(errors.containsError(createError("incorrect.total", "$.object_in_error.field4")));
    }

    private List<ObjectError> getAllErrors(){
        Object[] argument = {0,0,9999};
        FieldError fieldError1= new FieldError("object1","field1",null,true,null,argument,"value.outside.range");
        FieldError fieldError2= new FieldError("object2","field2",null,true,null,argument,"invalid.input.length");
        FieldError fieldError3= new FieldError("object3","field3",null,true,null,argument,"max.length.exceeded");
        FieldError fieldError4= new FieldError("object4","field4","incorrect.total");
        List<ObjectError> errorList = new ArrayList<>();
        errorList.add(fieldError1);
        errorList.add(fieldError2);
        errorList.add(fieldError3);
        errorList.add(fieldError4);
        return errorList;
    }

    private Error createRangeError(String error, String path, String upper, String lower) {
        Error returnError = new  Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
        returnError.addErrorValue("upper",upper);
        returnError.addErrorValue("lower",lower);
        return returnError;
    }
    private Error createMaxError(String error, String path, String maxLength) {
        Error returnError = new  Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
        returnError.addErrorValue("max_length",maxLength);
        return returnError;
    }

    private Error createError(String error, String path) {
        return new  Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());

    }
}
