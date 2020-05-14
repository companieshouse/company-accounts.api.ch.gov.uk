package uk.gov.companieshouse.api.accounts.validation;

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

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AfterCurrentPeriodImplTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AfterCurrentPeriodImpl afterCurrentPeriod;

    @Mock
    private LastAccounts accountingPeriod;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private CompanyAccount companyAccount;

    private static final LocalDate PERIOD_END_ON = LocalDate.of(2019, 1, 1);

    @Test
    @DisplayName("AfterCurrentPeriod - date after period")
    void afterCurrentPeriodDateAfterPeriod() {

        when(request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue())).thenReturn(companyAccount);
        when(companyAccount.getNextAccounts()).thenReturn(accountingPeriod);
        when(accountingPeriod.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertTrue(
                afterCurrentPeriod
                        .isValid(LocalDate.of(2019, 1, 2),
                                context));
    }

    @Test
    @DisplayName("AfterCurrentPeriod - date before period")
    void afterCurrentPeriodDateBeforePeriod() {

        when(request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue())).thenReturn(companyAccount);
        when(companyAccount.getNextAccounts()).thenReturn(accountingPeriod);
        when(accountingPeriod.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertFalse(
                afterCurrentPeriod
                        .isValid(LocalDate.of(2017, 12, 31),
                                context));
    }

    @Test
    @DisplayName("AfterCurrentPeriod - date equals period end")
    void afterCurrentPeriodDateEqualsPeriodStart() {

        when(request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue())).thenReturn(companyAccount);
        when(companyAccount.getNextAccounts()).thenReturn(accountingPeriod);
        when(accountingPeriod.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertFalse(
                afterCurrentPeriod
                        .isValid(PERIOD_END_ON, context));
    }

    @Test
    @DisplayName("AfterCurrentPeriod - null date")
    void afterCurrentPeriodNullDate() {

        assertTrue(afterCurrentPeriod.isValid(null, context));

        verify(request, never()).getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
    }
}