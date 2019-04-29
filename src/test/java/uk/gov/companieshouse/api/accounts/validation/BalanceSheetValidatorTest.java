package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CapitalAndReserves;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.MembersFunds;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BalanceSheetValidatorTest {

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private BalanceSheetValidator validator;

    @Mock
    private Transaction transaction;

    private static final String PERIOD_PATH = "periodPath";
    private static final String BALANCE_SHEET_PATH = PERIOD_PATH + ".balance_sheet";
    private static final String CALLED_UP_SHARE_CAPITAL_NOT_PAID_PATH = BALANCE_SHEET_PATH + ".called_up_share_capital_not_paid";
    private static final String FIXED_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";
    private static final String CURRENT_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".current_assets.total";
    private static final String CAPITAL_AND_RESERVES_PATH = BALANCE_SHEET_PATH + ".capital_and_reserves";
    private static final String TOTAL_SHAREHOLDER_FUNDS_PATH = CAPITAL_AND_RESERVES_PATH + ".total_shareholders_funds";
    private static final String MEMBERS_FUNDS_PATH = BALANCE_SHEET_PATH + ".members_funds";
    private static final String OTHER_LIABILITIES_OR_ASSETS_PATH = BALANCE_SHEET_PATH + ".other_liabilities_or_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".net_current_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_assets_less_current_liabilities";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_net_assets";

    private static final String UNEXPECTED_DATA_KEY = "unexpectedData";
    private static final String UNEXPECTED_DATA = "unexpected.data";

    private static final String INCORRECT_TOTAL_KEY = "incorrectTotal";
    private static final String INCORRECT_TOTAL = "incorrect.total";

    private static final String SHAREHOLDERS_FUNDS_MISMATCH_KEY = "shareholderFundsMismatch";
    private static final String SHAREHOLDERS_FUNDS_MISMATCH = "shareholders.mismatch";

    private static final String MANDATORY_ELEMENT_MISSING_KEY = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING = "mandatory.element.missing";

    @Test
    @DisplayName("Validate valid balance sheet - LBS filer")
    void validateValidBalanceSheetForLBSFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(3L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(7L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(4L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(1L);
        capitalAndReserves.setSharePremiumAccount(1L);
        capitalAndReserves.setOtherReserves(1L);
        capitalAndReserves.setProfitAndLoss(1L);
        capitalAndReserves.setTotalShareholdersFunds(4L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        when(companyService.isLBG(transaction)).thenReturn(false);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate valid balance sheet - LBG filer")
    void validateValidBalanceSheetForLBGFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(4L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        MembersFunds membersFunds = new MembersFunds();
        membersFunds.setProfitAndLossAccount(1L);
        membersFunds.setTotalMembersFunds(1L);
        balanceSheet.setMembersFunds(membersFunds);

        when(companyService.isLBG(transaction)).thenReturn(true);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate balance sheet including members' funds - LBS filer")
    void validateBalanceSheetIncludingMembersFundsForLBSFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(3L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(7L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(4L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(1L);
        capitalAndReserves.setSharePremiumAccount(1L);
        capitalAndReserves.setOtherReserves(1L);
        capitalAndReserves.setProfitAndLoss(1L);
        capitalAndReserves.setTotalShareholdersFunds(4L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        balanceSheet.setMembersFunds(new MembersFunds());

        when(companyService.isLBG(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, MEMBERS_FUNDS_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet including called up share capital not paid - LBG filer")
    void validateBalanceSheetIncludingCalledUpShareCapitalNotPaidForLBGFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(1L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(4L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        MembersFunds membersFunds = new MembersFunds();
        membersFunds.setProfitAndLossAccount(1L);
        membersFunds.setTotalMembersFunds(1L);
        balanceSheet.setMembersFunds(membersFunds);

        when(companyService.isLBG(transaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, CALLED_UP_SHARE_CAPITAL_NOT_PAID_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet including capital and reserves - LBG filer")
    void validateBalanceSheetIncludingCapitalAndReservesForLBGFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(4L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        balanceSheet.setCapitalAndReserves(new CapitalAndReserves());

        MembersFunds membersFunds = new MembersFunds();
        membersFunds.setProfitAndLossAccount(1L);
        membersFunds.setTotalMembersFunds(1L);
        balanceSheet.setMembersFunds(membersFunds);

        when(companyService.isLBG(transaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, CAPITAL_AND_RESERVES_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid fixed assets total - LBS filer")
    void validateBalanceSheetInvalidFixedAssetsTotalForLBSFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(3L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(2L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(7L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(4L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(1L);
        capitalAndReserves.setSharePremiumAccount(1L);
        capitalAndReserves.setOtherReserves(1L);
        capitalAndReserves.setProfitAndLoss(1L);
        capitalAndReserves.setTotalShareholdersFunds(4L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        when(companyService.isLBG(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, FIXED_ASSETS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid fixed assets total - LBG filer")
    void validateBalanceSheetInvalidFixedAssetsTotalForLBGFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(2L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(4L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        MembersFunds membersFunds = new MembersFunds();
        membersFunds.setProfitAndLossAccount(1L);
        membersFunds.setTotalMembersFunds(1L);
        balanceSheet.setMembersFunds(membersFunds);

        when(companyService.isLBG(transaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, FIXED_ASSETS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid current assets total - LBS filer")
    void validateBalanceSheetInvalidCurrentAssetsTotalForLBSFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(3L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(2L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(7L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(4L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(1L);
        capitalAndReserves.setSharePremiumAccount(1L);
        capitalAndReserves.setOtherReserves(1L);
        capitalAndReserves.setProfitAndLoss(1L);
        capitalAndReserves.setTotalShareholdersFunds(4L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        when(companyService.isLBG(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, CURRENT_ASSETS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid current assets total - LBG filer")
    void validateBalanceSheetInvalidCurrentAssetsTotalForLBGFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(2L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(4L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        MembersFunds membersFunds = new MembersFunds();
        membersFunds.setProfitAndLossAccount(1L);
        membersFunds.setTotalMembersFunds(1L);
        balanceSheet.setMembersFunds(membersFunds);

        when(companyService.isLBG(transaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, CURRENT_ASSETS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid net current assets - LBS filer")
    void validateBalanceSheetInvalidNetCurrentAssetsForLBSFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(3L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(2L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(7L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(4L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(1L);
        capitalAndReserves.setSharePremiumAccount(1L);
        capitalAndReserves.setOtherReserves(1L);
        capitalAndReserves.setProfitAndLoss(1L);
        capitalAndReserves.setTotalShareholdersFunds(4L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        when(companyService.isLBG(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid net current assets - LBG filer")
    void validateBalanceSheetInvalidNetCurrentAssetsForLBGFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(2L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(4L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        MembersFunds membersFunds = new MembersFunds();
        membersFunds.setProfitAndLossAccount(1L);
        membersFunds.setTotalMembersFunds(1L);
        balanceSheet.setMembersFunds(membersFunds);

        when(companyService.isLBG(transaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid total assets less liabilities - LBS filer")
    void validateBalanceSheetInvalidTotalAssetsLessLiabilitiesForLBSFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(4L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(7L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(4L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(1L);
        capitalAndReserves.setSharePremiumAccount(1L);
        capitalAndReserves.setOtherReserves(1L);
        capitalAndReserves.setProfitAndLoss(1L);
        capitalAndReserves.setTotalShareholdersFunds(4L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        when(companyService.isLBG(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid total assets less liabilities - LBG filer")
    void validateBalanceSheetInvalidTotalAssetsLessLiabilitiesForLBGFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(3L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(0L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        MembersFunds membersFunds = new MembersFunds();
        membersFunds.setProfitAndLossAccount(0L);
        membersFunds.setTotalMembersFunds(0L);
        balanceSheet.setMembersFunds(membersFunds);

        when(companyService.isLBG(transaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid total net assets - LBS filer")
    void validateBalanceSheetInvalidTotalNetAssetsForLBSFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(3L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(7L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(2L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(4L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(1L);
        capitalAndReserves.setSharePremiumAccount(1L);
        capitalAndReserves.setOtherReserves(1L);
        capitalAndReserves.setProfitAndLoss(1L);
        capitalAndReserves.setTotalShareholdersFunds(4L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        when(companyService.isLBG(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid total net assets - LBG filer")
    void validateBalanceSheetInvalidTotalNetAssetsForLBGFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(4L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(2L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        MembersFunds membersFunds = new MembersFunds();
        membersFunds.setProfitAndLossAccount(1L);
        membersFunds.setTotalMembersFunds(1L);
        balanceSheet.setMembersFunds(membersFunds);

        when(companyService.isLBG(transaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet with invalid shareholders' funds total - LBS filer")
    void validateBalanceSheetInvalidShareholdersFundsTotalForLBSFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(3L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(7L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(4L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(2L);
        capitalAndReserves.setSharePremiumAccount(1L);
        capitalAndReserves.setOtherReserves(1L);
        capitalAndReserves.setProfitAndLoss(1L);
        capitalAndReserves.setTotalShareholdersFunds(4L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        when(companyService.isLBG(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, TOTAL_SHAREHOLDER_FUNDS_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet shareholders' funds mismatch - LBS filer")
    void validateBalanceSheetWithShareholdersFundsMismatchForLBSFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(3L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(7L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(4L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(2L);
        capitalAndReserves.setSharePremiumAccount(1L);
        capitalAndReserves.setOtherReserves(1L);
        capitalAndReserves.setProfitAndLoss(1L);
        capitalAndReserves.setTotalShareholdersFunds(5L);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);

        when(companyService.isLBG(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, SHAREHOLDERS_FUNDS_MISMATCH_KEY, SHAREHOLDERS_FUNDS_MISMATCH);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(SHAREHOLDERS_FUNDS_MISMATCH, TOTAL_SHAREHOLDER_FUNDS_PATH)));
    }

    @Test
    @DisplayName("Validate balance sheet without capital and reserves - LBS filer")
    void validateBalanceSheetWithoutCapitalAndReservesForLBSFiler() throws ServiceException, DataException {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(3L);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(1L);
        fixedAssets.setTotal(1L);
        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(1L);
        currentAssets.setDebtors(1L);
        currentAssets.setCashAtBankAndInHand(1L);
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(1L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(1L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(3L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(7L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(4L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        when(companyService.isLBG(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_KEY, MANDATORY_ELEMENT_MISSING);

        Errors errors = new Errors();
        validator.validateBalanceSheet(balanceSheet, transaction, PERIOD_PATH, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING, CAPITAL_AND_RESERVES_PATH)));
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
