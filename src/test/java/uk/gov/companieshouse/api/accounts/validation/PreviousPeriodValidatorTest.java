package uk.gov.companieshouse.api.accounts.validation;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CapitalAndReserves;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodValidatorTest {

    private static final String PREVIOUS_PERIOD_PATH = "$.previous_period";
    private static final String BALANCE_SHEET_PATH = PREVIOUS_PERIOD_PATH + ".balance_sheet";
    private static final String FIXED_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";
    private static final String OTHER_LIABILITIES_OR_ASSETS_PATH = BALANCE_SHEET_PATH + ".other_liabilities_or_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".net_current_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_assets_less_current_liabilities";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_net_assets";
    private static final String CURRENT_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".current_assets.total";
    private static final String TOTAL_SHAREHOLDER_FUNDS_PATH = BALANCE_SHEET_PATH + ".capital_and_reserves.total_shareholders_funds";

    private BalanceSheet balanceSheet;
    private PreviousPeriod previousPeriod;
    private Errors errors;
    private PreviousPeriodValidator validator;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private CompanyService mockCompanyService;

    @Mock
    private ServiceException mockServiceException;

    @BeforeEach
    void setup() {

        previousPeriod = new PreviousPeriod();
        balanceSheet = new BalanceSheet();
        errors = new Errors();

        validator = new PreviousPeriodValidator(mockCompanyService);
    }

    @Test
    @DisplayName("SUCCESS - Test Balance Sheet validation with no errors")
    void validateBalanceSheet() throws DataException, ServiceException {

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setInvestments(1L);
        currentAssets.setTotal(4L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(4L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(5L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(5L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(2L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(2L);
        capitalAndReserves.setOtherReserves(0L);
        capitalAndReserves.setProfitAndLoss(0L);
        capitalAndReserves.setSharePremiumAccount(0L);
        capitalAndReserves.setTotalShareholdersFunds(2L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        balanceSheet.setCalledUpShareCapitalNotPaid(1L);

        previousPeriod.setBalanceSheet(balanceSheet);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        Errors errors = validator.validatePreviousPeriod(previousPeriod, mockTransaction);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("ERROR - Other Liabilities Or Assets - Test validation with net current assets error")
    void validateBalanceSheetWithOtherLiabilitiesOrAssetsNetCurrentAssetsError() throws DataException, ServiceException {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(4L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(2L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
        previousPeriod.setBalanceSheet(balanceSheet);

        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");
        ReflectionTestUtils.setField(validator, "mandatoryElementMissing", "mandatory_element_missing");

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        Errors errors =  validator.validatePreviousPeriod(previousPeriod, mockTransaction);

        assertTrue(errors.containsError(
                new Error("incorrect_total", OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("ERROR - Other Liabilities Or Assets - Test validation with total assets less current liabilities error")
    void validateBalanceSheetWithOtherLiabilitiesOrAssetsTotalAssetsLessCurrentLiabilitiesError() throws DataException, ServiceException {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(4L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(2L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(2L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(3L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(2L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        addInvalidFixedAssetsToBalanceSheet();
        previousPeriod.setBalanceSheet(balanceSheet);

        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        Errors errors = validator.validatePreviousPeriod(previousPeriod, mockTransaction);

        assertTrue(errors.containsError(

                new Error("incorrect_total", OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("ERROR - Other Liabilities Or Assets - Test validation with total net assets error")
    void validateBalanceSheetWithOtherLiabilitiesOrAssetsTotalNetAssetsError() throws DataException, ServiceException {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(4L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(2L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(2L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(2L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(2L);
        fixedAssets.setTotal(2L);
        balanceSheet.setFixedAssets(fixedAssets);

        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        Errors errors = validator.validatePreviousPeriod(previousPeriod, mockTransaction);

        assertTrue(errors.containsError(
                new Error("incorrect_total", OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("Test validate whole previous period with multiple errors")
    void validatePreviousPeriod() throws DataException, ServiceException {

        addInvalidFixedAssetsToBalanceSheet();
        addInvalidCurrentAssetsToBalanceSheet();

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setTotalNetAssets(15L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setTotalShareholdersFunds(10L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        previousPeriod.setBalanceSheet(balanceSheet);

        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");
        ReflectionTestUtils.setField(validator, "shareholderFundsMismatch", "shareholder_funds_mismatch");
        ReflectionTestUtils.setField(validator, "mandatoryElementMissing", "mandatory_element_missing");

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validatePreviousPeriod(previousPeriod, mockTransaction);

        assertTrue(errors.hasErrors());

        // Fixed assets error
        assertTrue(errors.containsError(
                new Error("incorrect_total", FIXED_ASSETS_TOTAL_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));

        // Current assets error
        assertTrue(errors.containsError(
                new Error("incorrect_total", CURRENT_ASSETS_TOTAL_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));

        // Other liabilities errors
        assertTrue(errors.containsError(
                new Error("incorrect_total", OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));

        assertTrue(errors.containsError(
                new Error("incorrect_total", OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));

        assertTrue(errors.containsError(
                new Error("incorrect_total", OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));

        // Capital and reserves errors
        assertTrue(errors.containsError(new Error("shareholder_funds_mismatch", TOTAL_SHAREHOLDER_FUNDS_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));

        assertTrue(errors.containsError(new Error("incorrect_total", TOTAL_SHAREHOLDER_FUNDS_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("ERROR - Test validation when single year filer files previous period")
    void validatePreviousPeriodOnFirstYearFiler() throws DataException, ServiceException {

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setNetCurrentAssets(1L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setTotal(1L);
        balanceSheet.setCurrentAssets(currentAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(1L);
        capitalAndReserves.setTotalShareholdersFunds(1L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);


        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "unexpectedData", "unexpected_data");

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(false);

        Errors errors = validator.validatePreviousPeriod(previousPeriod, mockTransaction);

        assertTrue(errors.containsError(
                new Error("unexpected_data", BALANCE_SHEET_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("Data exception thrown when company service API call fails")
    void testDataExceptionThrown() throws ServiceException {

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenThrow(mockServiceException);

        assertThrows(DataException.class,
                () -> validator.validatePreviousPeriod(previousPeriod, mockTransaction));
    }

    private void addInvalidFixedAssetsToBalanceSheet() {
        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(5L);
        fixedAssets.setTotal(10L);

        balanceSheet.setFixedAssets(fixedAssets);
    }

    private void addInvalidCurrentAssetsToBalanceSheet() {
        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(5L);
        currentAssets.setDebtors(5L);
        currentAssets.setCashAtBankAndInHand(5L);
        currentAssets.setInvestments(5L);
        currentAssets.setTotal(10L);

        balanceSheet.setCurrentAssets(currentAssets);
    }
}