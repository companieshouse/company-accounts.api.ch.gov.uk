package uk.gov.companieshouse.api.accounts.controller;

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
import uk.gov.companieshouse.api.accounts.links.LoansToDirectorsLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.AdditionalInformation;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoansToDirectorsAdditionalInformationControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String ADDITIONAL_INFORMATION_LINK = "additionalInformationLink";

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private AdditionalInformation additionalInformation;

    @Mock
    private ResourceService<AdditionalInformation> additionalInformationService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private LoansToDirectors loansToDirectors;

    @Mock
    private Map<String, String> loansToDirectorsLink;

    @Mock
    private Errors errors;

    @InjectMocks
    private LoansToDirectorsAdditionalInformationController additionalInformationController;

    @Test
    @DisplayName("Additional Information resource created successfully")
    void createAdditionalInformationResourceSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<AdditionalInformation> responseObject = new ResponseObject<>(ResponseStatus.CREATED,
                additionalInformation);
        when(additionalInformationService.create(additionalInformation, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                additionalInformationController.create(additionalInformation, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(additionalInformation, responseEntity.getBody());
    }

    @Test
    @DisplayName("Additional Information resource created with binding result errors")
    void createAdditionalInformationResourceWithBindingResultErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);

        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity returnedResponse =
                additionalInformationController.create(additionalInformation, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.BAD_REQUEST, returnedResponse.getStatusCode());
        assertEquals(errors, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Create additional information has failed - data exception thrown")
    void createAdditionalInformationResourceDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        DataException dataException = new DataException("");
        when(additionalInformationService.create(additionalInformation, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse())
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                additionalInformationController.create(additionalInformation, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update additional information resource - no additional information link")
    void updateAdditionalInformationResourceNoAdditionalInformationLink() {

        when(request.getAttribute(anyString())).thenReturn(loansToDirectors).thenReturn(transaction);
        when(loansToDirectors.getLinks()).thenReturn(loansToDirectorsLink);
        when(loansToDirectorsLink.get(LoansToDirectorsLinkType.ADDITIONAL_INFO.getLink())).thenReturn(null);

        ResponseEntity responseEntity =
                additionalInformationController.update(additionalInformation, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update additional Information resource - success")
    void updateAdditionalInformationResourceSuccess() throws DataException {

        mockTransactionAndLinks();

        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject<AdditionalInformation> responseObject = new ResponseObject<>(ResponseStatus.UPDATED,
                additionalInformation);
        when(additionalInformationService.update(additionalInformation, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                additionalInformationController.update(additionalInformation, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update additional Information resource - binding result errors")
    void updateAdditionalInformationResourceWithBindingResultErrors() {

        mockTransactionAndLinks();

        when(bindingResult.hasErrors()).thenReturn(true);

        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity returnedResponse =
                additionalInformationController.update(additionalInformation, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.BAD_REQUEST, returnedResponse.getStatusCode());
        assertEquals(errors, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Update additional information resource - data exception thrown")
    void updateAdditionalInformationResourceDataException() throws DataException {

        mockTransactionAndLinks();

        when(bindingResult.hasErrors()).thenReturn(false);

        when(additionalInformationService.update(additionalInformation, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                additionalInformationController.update(additionalInformation, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Get additional information resource - success")
    void getAdditionalInformationResourceSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<AdditionalInformation> responseObject = new ResponseObject<>(ResponseStatus.FOUND,
                additionalInformation);
        when(additionalInformationService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                additionalInformationController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(additionalInformation, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get additional information resource - data exception thrown")
    void getAdditionalInformationResourceDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        DataException dataException = new DataException("");
        when(additionalInformationService.find(COMPANY_ACCOUNTS_ID, request))
                .thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = additionalInformationController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Delete additional information resource - success")
    void deleteAdditionalInformationResourceSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(transaction);

        ResponseObject<AdditionalInformation> responseObject = new ResponseObject<>(ResponseStatus.UPDATED,
                additionalInformation);

        when(additionalInformationService.delete(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                additionalInformationController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Delete additional information resource - data exception thrown")
    void deleteAdditionalInformationResourceDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(transaction);

        DataException dataException = new DataException("");

        when(additionalInformationService.delete(COMPANY_ACCOUNTS_ID, request))
                .thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = additionalInformationController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    private void mockTransactionAndLinks() {
        when(request.getAttribute(anyString())).thenReturn(loansToDirectors).thenReturn(transaction);
        when(loansToDirectors.getLinks()).thenReturn(loansToDirectorsLink);
        when(loansToDirectorsLink.get(LoansToDirectorsLinkType.ADDITIONAL_INFO.getLink())).thenReturn(ADDITIONAL_INFORMATION_LINK);
    }
}