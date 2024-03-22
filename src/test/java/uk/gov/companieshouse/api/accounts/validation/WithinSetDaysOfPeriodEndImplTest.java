package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.exception.UncheckedDataException;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.account.CompanyAccountApi;
import uk.gov.companieshouse.api.model.company.account.NextAccountsApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WithinSetDaysOfPeriodEndImplTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WithinSetDaysOfPeriodEndImpl withinSetDaysOfPeriodEndImpl;

    @Mock
    private ConstraintValidatorContext context;
    
    @Mock
    private WithinSetDaysOfPeriodEnd withinSetDaysOfPeriodEnd;

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyService companyService;

    @Mock
    private CompanyProfileApi companyProfile;

    @Mock
    private CompanyAccountApi companyAccountApi;

    @Mock
    private NextAccountsApi nextAccountsApi;

    private static final String COMPANY_NUMBER = "companyNumber";

    private static final LocalDate PERIOD_END_ON = LocalDate.of(2019, 1, 1);
    
    @Test
    @DisplayName("Within set days of period end - before period end within constraint")
    void beforePeriodEndWithinConstraint() throws ServiceException {

        when(withinSetDaysOfPeriodEnd.numOfDays()).thenReturn(5); // constraint 5 days

        withinSetDaysOfPeriodEndImpl.initialize(withinSetDaysOfPeriodEnd);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfile);

        when(companyProfile.getAccounts()).thenReturn(companyAccountApi);

        when(companyAccountApi.getNextAccounts()).thenReturn(nextAccountsApi);

        when(nextAccountsApi.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertTrue(withinSetDaysOfPeriodEndImpl.isValid(PERIOD_END_ON.minusDays(4), context)); // 4 days before period end
    }

    @Test
    @DisplayName("Within set days of period end - before period end equals constraint")
    void beforePeriodEndEqualsConstraint() throws ServiceException {

        when(withinSetDaysOfPeriodEnd.numOfDays()).thenReturn(5); // constraint 5 days

        withinSetDaysOfPeriodEndImpl.initialize(withinSetDaysOfPeriodEnd);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfile);

        when(companyProfile.getAccounts()).thenReturn(companyAccountApi);

        when(companyAccountApi.getNextAccounts()).thenReturn(nextAccountsApi);

        when(nextAccountsApi.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertTrue(withinSetDaysOfPeriodEndImpl.isValid(PERIOD_END_ON.minusDays(5), context)); // 5 days before period end
    }

    @Test
    @DisplayName("Within set days of period end - before period end outside of constraint")
    void beforePeriodEndOutsideOfConstraint() throws ServiceException {

        when(withinSetDaysOfPeriodEnd.numOfDays()).thenReturn(5); // constraint 5 days

        withinSetDaysOfPeriodEndImpl.initialize(withinSetDaysOfPeriodEnd);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfile);

        when(companyProfile.getAccounts()).thenReturn(companyAccountApi);

        when(companyAccountApi.getNextAccounts()).thenReturn(nextAccountsApi);

        when(nextAccountsApi.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertFalse(withinSetDaysOfPeriodEndImpl.isValid(PERIOD_END_ON.minusDays(6), context)); // 6 days before period end
    }

    @Test
    @DisplayName("Within set days of period end - after period end within constraint")
    void afterPeriodEndWithinConstraint() throws ServiceException {

        when(withinSetDaysOfPeriodEnd.numOfDays()).thenReturn(5); // constraint 5 days

        withinSetDaysOfPeriodEndImpl.initialize(withinSetDaysOfPeriodEnd);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfile);

        when(companyProfile.getAccounts()).thenReturn(companyAccountApi);

        when(companyAccountApi.getNextAccounts()).thenReturn(nextAccountsApi);

        when(nextAccountsApi.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertTrue(withinSetDaysOfPeriodEndImpl.isValid(PERIOD_END_ON.plusDays(4), context)); // 4 days after period end
    }

    @Test
    @DisplayName("Within set days of period end - after period end equals constraint")
    void afterPeriodEndEqualsConstraint() throws ServiceException {

        when(withinSetDaysOfPeriodEnd.numOfDays()).thenReturn(5); // constraint 5 days

        withinSetDaysOfPeriodEndImpl.initialize(withinSetDaysOfPeriodEnd);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfile);

        when(companyProfile.getAccounts()).thenReturn(companyAccountApi);

        when(companyAccountApi.getNextAccounts()).thenReturn(nextAccountsApi);

        when(nextAccountsApi.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertTrue(withinSetDaysOfPeriodEndImpl.isValid(PERIOD_END_ON.plusDays(5), context)); // 5 days after period end
    }

    @Test
    @DisplayName("Within set days of period end - after period end outside of constraint")
    void afterPeriodEndOutsideOfConstraint() throws ServiceException {

        when(withinSetDaysOfPeriodEnd.numOfDays()).thenReturn(5); // constraint 5 days

        withinSetDaysOfPeriodEndImpl.initialize(withinSetDaysOfPeriodEnd);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfile);

        when(companyProfile.getAccounts()).thenReturn(companyAccountApi);

        when(companyAccountApi.getNextAccounts()).thenReturn(nextAccountsApi);

        when(nextAccountsApi.getPeriodEndOn()).thenReturn(PERIOD_END_ON);

        assertFalse(withinSetDaysOfPeriodEndImpl.isValid(PERIOD_END_ON.plusDays(6), context)); // 6 days after period end
    }

    @Test
    @DisplayName("Within set days of period end - null date permitted")
    void nullDatePermitted() {

        when(withinSetDaysOfPeriodEnd.numOfDays()).thenReturn(5);
        when(withinSetDaysOfPeriodEnd.allowNulls()).thenReturn(true);

        withinSetDaysOfPeriodEndImpl.initialize(withinSetDaysOfPeriodEnd);

        assertTrue(withinSetDaysOfPeriodEndImpl.isValid(null, context));
    }

    @Test
    @DisplayName("Within set days of period end - null date not permitted")
    void nullDateNotPermitted() {

        when(withinSetDaysOfPeriodEnd.numOfDays()).thenReturn(5);
        when(withinSetDaysOfPeriodEnd.allowNulls()).thenReturn(false);

        withinSetDaysOfPeriodEndImpl.initialize(withinSetDaysOfPeriodEnd);

        assertFalse(withinSetDaysOfPeriodEndImpl.isValid(null, context));
    }

    @Test
    @DisplayName("Within set days of period end - company service exception")
    void companyServiceException() throws ServiceException {

        when(withinSetDaysOfPeriodEnd.numOfDays()).thenReturn(5); // constraint 5 days

        withinSetDaysOfPeriodEndImpl.initialize(withinSetDaysOfPeriodEnd);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenThrow(ServiceException.class);

        LocalDate periodEndOnMinusFourDays = PERIOD_END_ON.minusDays(4);

        assertThrows(UncheckedDataException.class,
                () -> withinSetDaysOfPeriodEndImpl.isValid(periodEndOnMinusFourDays, context));
    }
}
