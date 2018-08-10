package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Equals;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession httpSessionMock;

    @Mock
    private Transaction transaction;

    @Mock
    private SmallFull smallFull;

    @Mock
    private CurrentPeriod currentPeriod;

    @Mock
    private CurrentPeriod createdCurrentPeriod;

    @Mock
    private CurrentPeriodService currentPeriodService;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private CurrentPeriodController currentPeriodController;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {
        doReturn(createdCurrentPeriod).when(currentPeriodService).save(any(CurrentPeriod.class), anyString());
        doReturn(httpSessionMock).when(request).getSession();
        doReturn(transaction).when(httpSessionMock).getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(smallFull).when(httpSessionMock).getAttribute(AttributeName.SMALLFULL.getValue());
        doReturn("123456").when(transaction).getCompanyNumber();
        doReturn(links).when(smallFull).getLinks();
        doReturn("7890").when(links).get("self");
    }

    @Test
    @DisplayName("Tests the successful creation of a currentPeriod resource")
    public void canCreateAccount() throws NoSuchAlgorithmException {
        ResponseEntity response = currentPeriodController.create(currentPeriod, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(new Equals(createdCurrentPeriod).matches(response.getBody()));
    }
}