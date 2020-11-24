package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CurrentPeriodValidatorTest {

    @Mock
    private BalanceSheetValidator balanceSheetValidator;

    @InjectMocks
    private CurrentPeriodValidator validator;

    @Mock
    private CurrentPeriod currentPeriod;

    @Mock
    private BalanceSheet balanceSheet;

    @Mock
    private Transaction transaction;

    private static final String PERIOD_PATH = "$.current_period";

    @Test
    @DisplayName("Validate current period")
    void validateCurrentPeriod() throws DataException {

        when(currentPeriod.getBalanceSheet()).thenReturn(balanceSheet);

        Errors errors = validator.validateCurrentPeriod(currentPeriod, transaction);

        assertNotNull(errors);
        verify(balanceSheetValidator).validateBalanceSheet(eq(balanceSheet), eq(transaction), eq(PERIOD_PATH), any(Errors.class));
    }
}