package uk.gov.companieshouse.api.accounts.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CurrentPeriodLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentPeriodInterceptorTest {

    @Mock
    private CurrentPeriod currentPeriod;

    @Mock
    private ResponseObject responseObject;

    @Mock
    private Transaction transaction;

    @Mock
    private SmallFull smallFull;

    @Mock
    private CurrentPeriodService currentPeriodService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Map<String, String> smallFullLinks;

    @Mock
    private Map<String, String> currentPeriodLinks;

    @InjectMocks
    private CurrentPeriodInterceptor currentPeriodInterceptor;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("transactionId", "5555");
        pathVariables.put("companyAccountId", "test");

        when(httpServletRequest.getHeader("X-Request-Id")).thenReturn("test");
        doReturn(transaction).when(httpServletRequest).getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(smallFull).when(httpServletRequest).getAttribute(AttributeName.SMALLFULL.getValue());
        doReturn(pathVariables).when(httpServletRequest).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        when(httpServletRequest.getMethod()).thenReturn("GET");
    }

    @Test
    @DisplayName("Tests the interceptor returns correctly when all is valid")
    public void testReturnsCorrectlyOnValidConditions() throws NoSuchAlgorithmException, DataException {
        when(currentPeriodService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(currentPeriod);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(currentPeriod.getLinks()).thenReturn(currentPeriodLinks);
        when(smallFullLinks.get(SmallFullLinkType.CURRENT_PERIOD.getLink())).thenReturn("linkToCurrentPeriod");
        when(currentPeriodLinks.get(CurrentPeriodLinkType.SELF.getLink())).thenReturn("linkToCurrentPeriod");

        currentPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());
        verify(currentPeriodService, times(1)).find(anyString(), any(HttpServletRequest.class));
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(CurrentPeriod.class));
    }

    @Test
    @DisplayName("Tests the interceptor returns false on a failed CurrentPeriodEntity lookup")
    public void testReturnsFalseForAFailedLookup() throws NoSuchAlgorithmException, DataException {
        doThrow(mock(DataException.class)).when(currentPeriodService).find(anyString(), any(HttpServletRequest.class));
        assertFalse(currentPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
    }

}
