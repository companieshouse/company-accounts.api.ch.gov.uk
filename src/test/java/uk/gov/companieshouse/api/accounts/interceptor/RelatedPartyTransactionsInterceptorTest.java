package uk.gov.companieshouse.api.accounts.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RelatedPartyTransactions;
import uk.gov.companieshouse.api.accounts.service.impl.RelatedPartyTransactionsServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RelatedPartyTransactionsInterceptorTest {

    @Mock
    private RelatedPartyTransactionsServiceImpl relatedPartyTransactionsService;

    @InjectMocks
    private RelatedPartyTransactionsInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Transaction transaction;

    @Mock
    private RelatedPartyTransactions relatedPartyTransactions;

    @Mock
    private ResponseObject responseObject;

    @Mock
    private SmallFull smallFull;

    @Mock
    private Map<String, String> smallFullLinks;

    @Mock
    private Map<String, String> relatedPartyTransactionsLinks;

    private static final String COMPANY_ACCOUNTS_ID_PATH_VAR = "companyAccountId";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String RELATED_PARTY_TRANSACTIONS_SELF_LINK = "relatedPartyTransactionsSelfLink";

    @BeforeEach
    void setUp() {

        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put(COMPANY_ACCOUNTS_ID_PATH_VAR, COMPANY_ACCOUNTS_ID);

        doReturn(pathVariables).when(request).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    @Test
    @DisplayName("Related party transactions interceptor - success")
    void relatedPartyTransactionsInterceptorSuccess() throws DataException {

        when(relatedPartyTransactionsService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(relatedPartyTransactions);

        doReturn(smallFull).when(request).getAttribute(AttributeName.SMALLFULL.getValue());
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(smallFullLinks.get(SmallFullLinkType.RELATED_PARTY_TRANSACTIONS.getLink())).thenReturn(
                RELATED_PARTY_TRANSACTIONS_SELF_LINK);

        when(relatedPartyTransactions.getLinks()).thenReturn(relatedPartyTransactionsLinks);
        when(relatedPartyTransactionsLinks.get(BasicLinkType.SELF.getLink())).thenReturn(
                RELATED_PARTY_TRANSACTIONS_SELF_LINK);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertTrue(preHandle);

        verify(request).setAttribute(AttributeName.RELATED_PARTY_TRANSACTIONS.getValue(), relatedPartyTransactions);
    }

    @Test
    @DisplayName("Related party transactions interceptor - related party transactions not found")
    void relatedPartyTransactionsInterceptorRelatedPartyTransactionNotFound() throws DataException {

        when(relatedPartyTransactionsService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Related party transactions interceptor - parent link not found")
    void relatedPartyTransactionsInterceptorParentLinkNotFound() throws DataException {

        when(relatedPartyTransactionsService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(relatedPartyTransactions);

        doReturn(smallFull).when(request).getAttribute(AttributeName.SMALLFULL.getValue());
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(smallFullLinks.get(SmallFullLinkType.RELATED_PARTY_TRANSACTIONS.getLink())).thenReturn(null);

        when(relatedPartyTransactions.getLinks()).thenReturn(relatedPartyTransactionsLinks);
        when(relatedPartyTransactionsLinks.get(BasicLinkType.SELF.getLink())).thenReturn(
                RELATED_PARTY_TRANSACTIONS_SELF_LINK);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Related party transactions interceptor - data exception")
    void relatedPartyTransactionsInterceptorDataException() throws DataException {

        when(relatedPartyTransactionsService.find(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
