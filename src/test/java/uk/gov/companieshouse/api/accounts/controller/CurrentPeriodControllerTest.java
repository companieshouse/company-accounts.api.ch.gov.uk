package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private CurrentPeriod currentPeriod;

    @Mock
    private SmallFull smallFull;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private CurrentPeriodService currentPeriodService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @InjectMocks
    private CurrentPeriodController currentPeriodController;

    @Test
    @DisplayName("Tests the successful creation of a currentPeriod resource")
    public void canCreateCurrentPeriod() throws DataException {
        when(request.getAttribute("transaction")).thenReturn(transaction);
        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED,
            currentPeriod);

        doReturn(responseObject).when(currentPeriodService)
            .create(any(CurrentPeriod.class), any(Transaction.class), anyString(),
                any(HttpServletRequest.class));
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(responseObject.getData());
        when(apiResponseMapper
            .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
            .thenReturn(responseEntity);

        ResponseEntity response = currentPeriodController
            .create(currentPeriod, bindingResult, "", request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(currentPeriod, response.getBody());
    }

    @Test
    @DisplayName("Test the retreval of a current period resource")
    public void canRetrieveCurrentPeriod() throws DataException {
        when(request.getAttribute("transaction")).thenReturn(transaction);
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(currentPeriod);
        doReturn(new ResponseObject(ResponseStatus.FOUND,
            currentPeriod)).when(currentPeriodService).find("123456", request);
        when(apiResponseMapper.mapGetResponse(currentPeriod,
            request)).thenReturn(responseEntity);

        ResponseEntity response = currentPeriodController.get("123456", request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(currentPeriod, response.getBody());
    }

    @Test
    @DisplayName("Tests the successful update of a currentPeriod resource")
    public void canUpdateCurrentPeriod() throws DataException {
        doReturn(smallFull).when(request)
            .getAttribute(AttributeName.SMALLFULL.getValue());
        HashMap<String, String> links = new HashMap<>();
        links.put(SmallFullLinkType.CURRENT_PERIOD.getLink(), "link");
        when(smallFull.getLinks()).thenReturn(links);
        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
            currentPeriod);
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(),
            null, responseObject.getErrors()))
            .thenReturn(responseEntity);
        doReturn(responseObject).when(currentPeriodService)
            .update(currentPeriod, null, "12345", request);

        ResponseEntity response = currentPeriodController
            .update(currentPeriod, bindingResult, "12345", request);



        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Tests the unsuccessful update of a currentPeriod resource")
    public void canUpdateCurrentPeriodFail() throws DataException {
        doReturn(smallFull).when(request)
            .getAttribute(AttributeName.SMALLFULL.getValue());
        HashMap<String, String> links = new HashMap<>();
        when(smallFull.getLinks()).thenReturn(links);
        ResponseEntity response = currentPeriodController
            .update(currentPeriod, bindingResult, "123456", request);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
