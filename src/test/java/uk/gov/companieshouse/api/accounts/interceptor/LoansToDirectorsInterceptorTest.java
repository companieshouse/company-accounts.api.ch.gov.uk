package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerMapping;

import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.service.impl.LoansToDirectorsServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class LoansToDirectorsInterceptorTest {

    @Mock
    private LoansToDirectorsServiceImpl loansToDirectorsService;

    @InjectMocks
    private LoansToDirectorsInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Transaction transaction;

    @Mock
    private LoansToDirectors loansToDirectors;

    @Mock
    private ResponseObject responseObject;

    @Mock
    private SmallFull smallFull;

    @Mock
    private Map<String, String> smallFullLinks;

    @Mock
    private Map<String, String> loansToDirectorsLinks;

    private static final String COMPANY_ACCOUNTS_ID_PATH_VAR = "companyAccountId";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String LOANS_TO_DIRECTORS_SELF_LINK = "loansToDirectorsSelfLink";

    @BeforeEach
    void setUp () {

        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put(COMPANY_ACCOUNTS_ID_PATH_VAR, COMPANY_ACCOUNTS_ID);

        doReturn(pathVariables).when(request).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    @Test
    @DisplayName("Loans to directors interceptor - success")
    void loansToDirectorsInterceptorSuccess() throws DataException {

        when(loansToDirectorsService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(loansToDirectors);

        doReturn(smallFull).when(request).getAttribute(AttributeName.SMALLFULL.getValue());
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(smallFullLinks.get(SmallFullLinkType.LOANS_TO_DIRECTORS.getLink())).thenReturn(
                LOANS_TO_DIRECTORS_SELF_LINK);

        when(loansToDirectors.getLinks()).thenReturn(loansToDirectorsLinks);
        when(loansToDirectorsLinks.get(BasicLinkType.SELF.getLink())).thenReturn(
                LOANS_TO_DIRECTORS_SELF_LINK);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertTrue(preHandle);

        verify(request).setAttribute(AttributeName.LOANS_TO_DIRECTORS.getValue(), loansToDirectors);
    }

    @Test
    @DisplayName("Loan to directors interceptor - loans to directors not found")
    void loansToDirectorsInterceptorLoansToDirectorsNotFound() throws DataException {

        when(loansToDirectorsService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Loans to directors interceptor - parent link not found")
    void loansToDirectorsInterceptorParentLinkNotFound() throws DataException {

        when(loansToDirectorsService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(loansToDirectors);

        doReturn(smallFull).when(request).getAttribute(AttributeName.SMALLFULL.getValue());
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(smallFullLinks.get(SmallFullLinkType.LOANS_TO_DIRECTORS.getLink())).thenReturn(null);

        when(loansToDirectors.getLinks()).thenReturn(loansToDirectorsLinks);
        when(loansToDirectorsLinks.get(BasicLinkType.SELF.getLink())).thenReturn(
                LOANS_TO_DIRECTORS_SELF_LINK);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Loans to directors interceptor - data exception")
    void loansToDirectorsInterceptorDataException() throws DataException {

        when(loansToDirectorsService.find(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
