package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.LastAccounts;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WithinCurrentPeriodImplTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WithinCurrentPeriodImpl withinCurrentPeriodImpl;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private LastAccounts accountingPeriod;

    private static final LocalDate PERIOD_START_ON = LocalDate.of(2018, 1, 1);
    private static final LocalDate PERIOD_END_ON = LocalDate.of(2019, 1, 1);

    @Test
    @DisplayName("WithinCurrentPeriod - date after period")
    void withinCurrentPeriodDateAfterPeriod() {

        when(request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue())).thenReturn(companyAccount);
        when(companyAccount.getNextAccounts()).thenReturn(accountingPeriod);
        when(accountingPeriod.getPeriodStartOn()).thenReturn(PERIOD_START_ON);
        when(accountingPeriod.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertFalse(
                withinCurrentPeriodImpl
                        .isValid(LocalDate.of(2019, 1, 2),
                                context));
    }

    @Test
    @DisplayName("WithinCurrentPeriod - date before period")
    void withinCurrentPeriodDateBeforePeriod() {

        when(request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue())).thenReturn(companyAccount);
        when(companyAccount.getNextAccounts()).thenReturn(accountingPeriod);
        when(accountingPeriod.getPeriodStartOn()).thenReturn(PERIOD_START_ON);
        when(accountingPeriod.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertFalse(
                withinCurrentPeriodImpl
                        .isValid(LocalDate.of(2017, 12, 31),
                                context));
    }

    @Test
    @DisplayName("WithinCurrentPeriod - date during period")
    void withinCurrentPeriodDateDuringPeriod() {

        when(request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue())).thenReturn(companyAccount);
        when(companyAccount.getNextAccounts()).thenReturn(accountingPeriod);
        when(accountingPeriod.getPeriodStartOn()).thenReturn(PERIOD_START_ON);
        when(accountingPeriod.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertTrue(
                withinCurrentPeriodImpl
                        .isValid(LocalDate.of(2018, 7, 1),
                                context));
    }

    @Test
    @DisplayName("WithinCurrentPeriod - date equals period start")
    void withinCurrentPeriodDateEqualsPeriodStart() {

        when(request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue())).thenReturn(companyAccount);
        when(companyAccount.getNextAccounts()).thenReturn(accountingPeriod);
        when(accountingPeriod.getPeriodStartOn()).thenReturn(PERIOD_START_ON);
        when(accountingPeriod.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertTrue(
                withinCurrentPeriodImpl
                        .isValid(PERIOD_START_ON, context));
    }

    @Test
    @DisplayName("WithinCurrentPeriod - date equals period end")
    void withinCurrentPeriodDateEqualsPeriodEnd() {

        when(request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue())).thenReturn(companyAccount);
        when(companyAccount.getNextAccounts()).thenReturn(accountingPeriod);
        when(accountingPeriod.getPeriodStartOn()).thenReturn(PERIOD_START_ON);
        when(accountingPeriod.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertTrue(
                withinCurrentPeriodImpl
                        .isValid(PERIOD_END_ON, context));
    }
    
    @Test
    @DisplayName("WithinCurrentPeriod - null date")
    void withinCurrentPeriodNullDate() {
        
        assertTrue(withinCurrentPeriodImpl.isValid(null, context));
        
        verify(request, never()).getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
    }
}
