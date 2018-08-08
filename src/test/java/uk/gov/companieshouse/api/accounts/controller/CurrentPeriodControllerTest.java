package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.CurrentPeriodService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession httpSessionMock;

    @Mock
    private CompanyAccountEntity companyAccountEntity;

    @Mock
    private SmallFull smallFull;

    @Mock
    private HttpSession session;

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

        when(currentPeriodService.save(any(CurrentPeriod.class), anyString()))
                .thenReturn(createdCurrentPeriod);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue()))
                .thenReturn(companyAccountEntity);
        when(companyAccountEntity.getId()).thenReturn("123456");

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