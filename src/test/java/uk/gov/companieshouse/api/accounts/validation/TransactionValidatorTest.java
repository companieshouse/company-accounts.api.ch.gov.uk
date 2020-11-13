package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransaction;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransactionBreakdown;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionValidatorTest {

    private static final String TRANSACTION_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START = "$.transaction.breakdown.balance_at_period_start";

    private static final String RELATED_PARTY_NAME = "relatedPartyName";

    private static final String RPT_DESCRIPTION = "description";

    private static final String UNEXPECTED_DATA = "unexpected.data";

    private static final String UNEXPECTED_DATA_KEY = "unexpectedData";

    @Mock
    private Transaction transaction;

    @Mock
    private Errors errors;

    @Mock
    private CompanyService companyService;

    private RptTransaction rptTransaction;

    private TransactionValidator transactionValidator;

    @BeforeEach
    void setup() {
        rptTransaction = new RptTransaction();
        transactionValidator = new TransactionValidator(companyService);
    }

    @Test
    @DisplayName("RptTransaction validation with valid Transaction and breakdown for multi year filer, with balance at period start")
    void testSuccessRptTransactionValidationForSingleYearFiler() throws DataException, ServiceException {

        rptTransaction.setNameOfRelatedParty(RELATED_PARTY_NAME);
        rptTransaction.setDescription(RPT_DESCRIPTION);


        createValidSingleYearFilerRptTransactionBreakdown();

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        errors = transactionValidator.validateRptTransaction(rptTransaction, transaction);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("RptTransaction validation with returns validation year for single year filer with balance at period start")
    void testRptTransactionValidationForSingleYearFilerValidationError() throws DataException, ServiceException {

        rptTransaction.setNameOfRelatedParty(RELATED_PARTY_NAME);
        rptTransaction.setDescription(RPT_DESCRIPTION);

        createValidMultiYearFilerRptTransactionBreakdown();

        ReflectionTestUtils.setField(transactionValidator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        errors = transactionValidator.validateRptTransaction(rptTransaction, transaction);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, TRANSACTION_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START)));
    }

    private void createValidMultiYearFilerRptTransactionBreakdown() {
        RptTransactionBreakdown rptTransactionBreakdown = new RptTransactionBreakdown();
        rptTransactionBreakdown.setBalanceAtPeriodStart(10L);
        rptTransactionBreakdown.setBalanceAtPeriodEnd(20L);

        rptTransaction.setBreakdown(rptTransactionBreakdown);
    }

    private void createValidSingleYearFilerRptTransactionBreakdown() {

        RptTransactionBreakdown rptTransactionBreakdown = new RptTransactionBreakdown();
        rptTransactionBreakdown.setBalanceAtPeriodEnd(20L);

        rptTransaction.setBreakdown(rptTransactionBreakdown);
    }

    private Error createError(String error, String path) {

        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }

}