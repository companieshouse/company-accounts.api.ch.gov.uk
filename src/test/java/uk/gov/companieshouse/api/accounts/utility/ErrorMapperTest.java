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
    ErrorMapper errorMapper;

    @Mock
    Environment environment;

    @Mock
    private BindingResult mockBindingResult;

    private Errors errors;

    @Test
    @DisplayName("Test Mapping Error to Error Model correctly ")
    void testMapErrorToErrorModel() {
        when(mockBindingResult.getAllErrors()).thenReturn(getAllErrors());
        when(environment.resolvePlaceholders(any())).thenReturn("error_string");
        errors = errorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult);
        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        //assertTrue(errors.containsError(createError("error_string", "$.object1.field1")));
        //assertTrue(errors.containsError(createError("error_string", "$.object2.field2")));
        //assertTrue(errors.containsError(createError("error_string", "$.object3.field3")));
    }
    private List<ObjectError> getAllErrors(){
        Object[] argument = {0,0,9999};
        FieldError fieldError1= new FieldError("object1","field1",null,true,null,argument,"value.outside.range");
        FieldError fieldError2= new FieldError("object2","field2",null,true,null,argument,"invalid.input.length");
        FieldError fieldError3= new FieldError("object3","field3",null,true,null,argument,"max.length.exceeded");
        FieldError fieldError4= new FieldError("object4","field4","incorrect.total");
        List errorList = new ArrayList();
        errorList.add(fieldError1);
        errorList.add(fieldError2);
        errorList.add(fieldError3);
        errorList.add(fieldError4);
        return errorList;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
