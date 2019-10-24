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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class PreviousPeriodInterceptorTest {

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private ResponseObject responseObject;

    @Mock
    private Transaction transaction;

    @Mock
    private SmallFull smallFull;

    @Mock
    private PreviousPeriodService previousPeriodService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Map<String, String> smallFullLinks;

    @Mock
    private Map<String, String> previousPeriodLinks;

    @InjectMocks
    private PreviousPeriodInterceptor previousPeriodInterceptor;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {

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
    void testReturnsCorrectlyOnValidConditions() throws NoSuchAlgorithmException, DataException {
        when(previousPeriodService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(previousPeriod);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(previousPeriod.getLinks()).thenReturn(previousPeriodLinks);
        when(smallFullLinks.get(SmallFullLinkType.PREVIOUS_PERIOD.getLink())).thenReturn("linkToPreviousPeriod");
        when(previousPeriodLinks.get("self")).thenReturn("linkToPreviousPeriod");

        previousPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());
        verify(previousPeriodService, times(1)).find(anyString(), any(HttpServletRequest.class));
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(PreviousPeriod.class));
    }

    @Test
    @DisplayName("Tests the interceptor returns false on a failed PreviousPeriodEntity lookup")
    void testReturnsFalseForAFailedLookup() throws NoSuchAlgorithmException, DataException {
        doThrow(mock(DataException.class)).when(previousPeriodService).find(anyString(), any(HttpServletRequest.class));
        assertFalse(previousPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
    }

}
