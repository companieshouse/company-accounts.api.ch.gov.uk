package uk.gov.companieshouse.api.accounts.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodControllerTest {

    public static final String X_REQUEST_ID = "X-Request-Id";
    public static final String TRANSACTION = "transaction";
    public static final String TEST = "test";
    public static final String CREATE = "create";
    public static final String FIND = "find";
    public static final String SELF = "self";
    @Mock
    private HttpServletRequest request;

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyAccountEntity companyAccountEntity;

    @Mock
    private PreviousPeriodService previousPeriodService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private SmallFull smallFull;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private PreviousPeriodController previousPeriodController;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException, DataException {
        when(request.getAttribute(TRANSACTION)).thenReturn(transaction);
        when(request.getHeader(X_REQUEST_ID)).thenReturn(TEST);
        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED,
            previousPeriod);
        doReturn(responseObject).when(previousPeriodService)
            .create(any(PreviousPeriod.class), any(Transaction.class), anyString(), anyString());
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(),
            responseObject.getData(), responseObject.getValidationErrorData()))
            .thenReturn(responseEntity);
        doReturn(transaction).when(request)
            .getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(smallFull).when(request).getAttribute(AttributeName.SMALLFULL.getValue());
        doReturn(companyAccountEntity).when(request)
            .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        doReturn("12345").when(companyAccountEntity).getId();
        doReturn(responseObject).when(previousPeriodService).findById(CREATE, TEST);
        doReturn(new ResponseObject(ResponseStatus.FOUND,
            previousPeriod)).when(previousPeriodService).findById(FIND, TEST);
        doReturn("123456").when(transaction).getCompanyNumber();
        doReturn(links).when(smallFull).getLinks();
        doReturn("7890").when(links).get(SELF);
    }

    @Test
    @DisplayName("Tests the successful creation of a previous period resource")
    public void canCreatePreviousPeriod() throws NoSuchAlgorithmException {
        ResponseEntity response = previousPeriodController
            .create(previousPeriod, request);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(previousPeriod, response.getBody());
    }


}
