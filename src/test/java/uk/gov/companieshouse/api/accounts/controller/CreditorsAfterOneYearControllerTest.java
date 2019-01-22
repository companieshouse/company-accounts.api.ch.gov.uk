package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CreditorsAfterOneYear;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CreditorsAfterOneYearService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreditorsAfterOneYearControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private CreditorsAfterOneYear mockCreditorsAfterOneYear;

    @Mock
    private CreditorsAfterOneYearService mockCreditorsAfterOneYearService;

    @Mock
    private ApiResponseMapper mockApiResponseMapper;

    @Mock
    private ErrorMapper mockErrorMapper;

    @InjectMocks
    private CreditorsAfterOneYearController controller;

    @Test
    @DisplayName("Creditors after one year resource created successfully")
    void createCreditorsAfterOneYearResource() throws DataException {

        when(mockBindingResult.hasErrors()).thenReturn(false);

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED,
                mockCreditorsAfterOneYear);
        when(mockCreditorsAfterOneYearService.create(mockCreditorsAfterOneYear, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(mockApiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.create(mockCreditorsAfterOneYear, mockBindingResult, COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockCreditorsAfterOneYear, responseEntity.getBody());
    }

    @Test
    @DisplayName("Create creditors after one year has failed - data exception thrown")
    void createCreditorsAfterOneYearDataException() throws DataException {

        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        DataException dataException = new DataException("");
        when(mockCreditorsAfterOneYearService.create(mockCreditorsAfterOneYear, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(dataException);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.map(dataException))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.create(mockCreditorsAfterOneYear, mockBindingResult, COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Create creditors after one year - has binding errors")
    void createCreditorsAfterOneYearBindingErrors() {

        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockErrorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                controller.create(mockCreditorsAfterOneYear, mockBindingResult, COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}
