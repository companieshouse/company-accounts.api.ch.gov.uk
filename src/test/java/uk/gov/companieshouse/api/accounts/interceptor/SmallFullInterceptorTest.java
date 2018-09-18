package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullInterceptorTest {

    @Mock
    private SmallFull smallFull;

    @Mock
    private ResponseObject responseObject;

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyAccountEntity companyAccountEntity;

    @Mock
    private CompanyAccountDataEntity companyAccountDataEntity;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Map<String, String> companyAccountLinks;

    @Mock
    private Map<String, String> smallFullLinks;

    @InjectMocks
    private SmallFullInterceptor smallFullInterceptor;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("transactionId", "5555");

        when(httpServletRequest.getHeader("X-Request-Id")).thenReturn("test");
        doReturn(transaction).when(httpServletRequest).getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(companyAccountEntity).when(httpServletRequest).getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        doReturn(pathVariables).when(httpServletRequest).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        when(companyAccountEntity.getId()).thenReturn("test");
        when(smallFullService.generateID(anyString())).thenReturn("test");
        when(httpServletRequest.getMethod()).thenReturn("GET");
    }

    @Test
    @DisplayName("Tests the interceptor returns correctly when all is valid")
    public void testReturnsCorrectlyOnValidConditions() throws NoSuchAlgorithmException, DataException {
        when(smallFullService.findById(anyString(), anyString())).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(smallFull);
        when(companyAccountEntity.getData()).thenReturn(companyAccountDataEntity);
        when(companyAccountDataEntity.getLinks()).thenReturn(companyAccountLinks);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(companyAccountLinks.get("small_full_accounts")).thenReturn("linkToSmallFull");
        when(smallFullLinks.get("self")).thenReturn("linkToSmallFull");

        smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());
        verify(smallFullService, times(1)).findById(anyString(), anyString());
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(SmallFull.class));
    }

    @Test
    @DisplayName("Tests the interceptor returns false on a failed SmallFullEntity lookup")
    public void testReturnsFalseForAFailedLookup() throws NoSuchAlgorithmException, DataException {
        doThrow(mock(DataException.class)).when(smallFullService).findById(anyString(), anyString());
        assertFalse(smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
    }
}