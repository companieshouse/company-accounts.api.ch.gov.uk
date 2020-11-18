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

    private static final String MANDATORY_ELEMENT_MISSING_KEY = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING = "mandatory.element.missing";

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

        createMultiYearFilerRptTransactionBreakdown(true);

        ReflectionTestUtils.setField(transactionValidator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        errors = transactionValidator.validateRptTransaction(rptTransaction, transaction);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, TRANSACTION_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START)));
    }

    @Test
    @DisplayName("RptTransaction validation - multi year filer with no period start")
    void testRptTransactionValidationForMultiYearFilerValidationError() throws DataException, ServiceException {

        rptTransaction.setNameOfRelatedParty(RELATED_PARTY_NAME);
        rptTransaction.setDescription(RPT_DESCRIPTION);

        createMultiYearFilerRptTransactionBreakdown(false);

        ReflectionTestUtils.setField(transactionValidator, MANDATORY_ELEMENT_MISSING_KEY, MANDATORY_ELEMENT_MISSING);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        errors = transactionValidator.validateRptTransaction(rptTransaction, transaction);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING, TRANSACTION_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START)));
    }


    @Test
    @DisplayName("RptTransaction validation - multi year filer with period start")
    void testRptTransactionValidationForMultiYearFilerValid() throws DataException, ServiceException {

        rptTransaction.setNameOfRelatedParty(RELATED_PARTY_NAME);
        rptTransaction.setDescription(RPT_DESCRIPTION);

        createMultiYearFilerRptTransactionBreakdown(true);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        errors = transactionValidator.validateRptTransaction(rptTransaction, transaction);

        assertEquals(0, errors.getErrorCount());
    }

    private void createMultiYearFilerRptTransactionBreakdown(boolean includePeriodStart) {
        RptTransactionBreakdown rptTransactionBreakdown = new RptTransactionBreakdown();
        rptTransactionBreakdown.setBalanceAtPeriodEnd(20L);

        if (includePeriodStart) {
            rptTransactionBreakdown.setBalanceAtPeriodStart(10L);

        }

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