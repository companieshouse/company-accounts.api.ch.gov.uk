package uk.gov.companieshouse.api.accounts.validation;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CapitalAndReserves;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.MembersFunds;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class BalanceSheetValidator extends BaseValidator {

    @Autowired
    private CompanyService companyService;

    @Value("${shareholders.mismatch}")
    private String shareholderFundsMismatch;

    @Value("${membersFunds.mismatch}")
    private String membersFundsMismatch;

    private static final String BALANCE_SHEET_PATH = ".balance_sheet";
    private static final String CALLED_UP_SHARE_CAPITAL_NOT_PAID_PATH = BALANCE_SHEET_PATH + ".called_up_share_capital_not_paid";
    private static final String FIXED_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";
    private static final String CURRENT_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".current_assets.total";
    private static final String CAPITAL_AND_RESERVES_PATH = BALANCE_SHEET_PATH + ".capital_and_reserves";
    private static final String TOTAL_SHAREHOLDER_FUNDS_PATH = CAPITAL_AND_RESERVES_PATH + ".total_shareholders_funds";
    private static final String MEMBERS_FUNDS_PATH = BALANCE_SHEET_PATH + ".members_funds";
    private static final String TOTAL_MEMBERS_FUNDS_PATH = MEMBERS_FUNDS_PATH + ".total_members_funds";
    private static final String OTHER_LIABILITIES_OR_ASSETS_PATH = BALANCE_SHEET_PATH + ".other_liabilities_or_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".net_current_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_assets_less_current_liabilities";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_net_assets";

    public void validateBalanceSheet(BalanceSheet balanceSheet, Transaction transaction, String periodPath, Errors errors) throws DataException {

        boolean isLBGCompanyFiling = getIsLBGCompanyFiling(transaction);

        validateTotalFixedAssets(balanceSheet, periodPath, errors);
        validateTotalCurrentAssets(balanceSheet, periodPath, errors);
        validateTotalOtherLiabilitiesOrAssets(balanceSheet, isLBGCompanyFiling, periodPath, errors);

        if (isLBGCompanyFiling) {

            validateCalledUpShareCapitalNotPaidNotSubmitted(balanceSheet, periodPath, errors);
            validateCapitalAndReservesNotSubmitted(balanceSheet, periodPath, errors);
            validateTotalMembersFunds(balanceSheet, periodPath, errors);
        } else {

            validateMembersFundsNotSubmitted(balanceSheet, periodPath, errors);
            validateTotalShareholderFunds(balanceSheet, periodPath, errors);
        }
    }

    private void validateTotalFixedAssets(BalanceSheet balanceSheet, String periodPath, Errors errors) {

        FixedAssets fixedAssets = balanceSheet.getFixedAssets();

        if (fixedAssets != null) {

            Long tangible = Optional.ofNullable(fixedAssets.getTangible()).orElse(0L);
            Long investments = Optional.ofNullable(fixedAssets.getInvestments()).orElse(0L);
            Long fixedAssetsTotal = Optional.ofNullable(fixedAssets.getTotal()).orElse(0L);

            Long calculatedTotal = tangible + investments;

            validateAggregateTotal(fixedAssetsTotal, calculatedTotal, periodPath + FIXED_ASSETS_TOTAL_PATH, errors);
        }
    }

    private void validateTotalCurrentAssets(BalanceSheet balanceSheet, String periodPath, Errors errors) {

        CurrentAssets currentAssets = balanceSheet.getCurrentAssets();

        if (currentAssets != null) {

            Long stocks = Optional.ofNullable(currentAssets.getStocks()).orElse(0L);
            Long debtors = Optional.ofNullable(currentAssets.getDebtors()).orElse(0L);
            Long cashAtBankAndInHand = Optional.ofNullable(currentAssets.getCashAtBankAndInHand()).orElse(0L);
            Long investments = Optional.ofNullable(currentAssets.getInvestments()).orElse(0L);
            Long currentAssetsTotal = Optional.ofNullable(currentAssets.getTotal()).orElse(0L);

            Long calculatedTotal = stocks + debtors + cashAtBankAndInHand + investments;

            validateAggregateTotal(currentAssetsTotal, calculatedTotal, periodPath + CURRENT_ASSETS_TOTAL_PATH, errors);
        }
    }

    private void validateTotalOtherLiabilitiesOrAssets(BalanceSheet balanceSheet, boolean isLBGCompanyFiling, String periodPath, Errors errors) {

        if (balanceSheet.getOtherLiabilitiesOrAssets() != null) {

            calculateOtherLiabilitiesOrAssetsNetCurrentAssets(balanceSheet, periodPath, errors);
            calculateOtherLiabilitiesOrAssetsTotalAssetsLessCurrentLiabilities(balanceSheet, isLBGCompanyFiling, periodPath, errors);
            calculateOtherLiabilitiesOrAssetsTotalNetAssets(balanceSheet, periodPath, errors);
            checkOtherLiabilitiesAreMandatory(balanceSheet, periodPath, errors);
        }
    }

    private void calculateOtherLiabilitiesOrAssetsNetCurrentAssets(BalanceSheet balanceSheet, String periodPath, Errors errors) {

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = balanceSheet.getOtherLiabilitiesOrAssets();

        Long prepaymentsAndAccruedIncome = Optional.ofNullable(otherLiabilitiesOrAssets.getPrepaymentsAndAccruedIncome()).orElse(0L);
        Long creditorsDueWithinOneYear = Optional.ofNullable(otherLiabilitiesOrAssets.getCreditorsDueWithinOneYear()).orElse(0L);

        Long totalCurrentAssets = 0L;
        if (balanceSheet.getCurrentAssets() != null) {
            totalCurrentAssets = balanceSheet.getCurrentAssets().getTotal();
        }

        Long calculatedTotal = totalCurrentAssets + prepaymentsAndAccruedIncome - creditorsDueWithinOneYear;

        Long netCurrentAssets = Optional.ofNullable(otherLiabilitiesOrAssets.getNetCurrentAssets()).orElse(0L);
        validateAggregateTotal(netCurrentAssets, calculatedTotal, periodPath + OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH, errors);
    }

    private void calculateOtherLiabilitiesOrAssetsTotalAssetsLessCurrentLiabilities(BalanceSheet balanceSheet, boolean isLBGCompanyFiling, String periodPath, Errors errors) {

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = balanceSheet.getOtherLiabilitiesOrAssets();

        Long netCurrentAssets = Optional.ofNullable(otherLiabilitiesOrAssets.getNetCurrentAssets()).orElse(0L);

        Long fixedAssetsTotal = 0L;
        if (balanceSheet.getFixedAssets() != null) {
            fixedAssetsTotal = Optional.ofNullable(balanceSheet.getFixedAssets().getTotal()).orElse(0L);
        }

        Long calculatedTotal = fixedAssetsTotal + netCurrentAssets;

        if (!isLBGCompanyFiling) {
            Long calledUpShareCapitalNotPaid = Optional.ofNullable(balanceSheet.getCalledUpShareCapitalNotPaid()).orElse(0L);
            calculatedTotal += calledUpShareCapitalNotPaid;
        }

        Long totalAssetsLessCurrentLiabilities = Optional.ofNullable(otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities()).orElse(0L);
        validateAggregateTotal(totalAssetsLessCurrentLiabilities, calculatedTotal, periodPath + OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH, errors);
    }

    private void calculateOtherLiabilitiesOrAssetsTotalNetAssets(BalanceSheet balanceSheet, String periodPath, Errors errors) {

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = balanceSheet.getOtherLiabilitiesOrAssets();

        Long totalAssetsLessCurrentLiabilities = Optional.ofNullable(otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities()).orElse(0L);
        Long creditorsAfterOneYear = Optional.ofNullable(otherLiabilitiesOrAssets.getCreditorsAfterOneYear()).orElse(0L);
        Long accrualsAndDeferredIncome = Optional.ofNullable(otherLiabilitiesOrAssets.getAccrualsAndDeferredIncome()).orElse(0L);
        Long provisionForLiabilities = Optional.ofNullable(otherLiabilitiesOrAssets.getProvisionForLiabilities()).orElse(0L);

        Long calculatedTotal = totalAssetsLessCurrentLiabilities - (creditorsAfterOneYear + accrualsAndDeferredIncome + provisionForLiabilities);

        Long totalNetAssets = Optional.ofNullable(otherLiabilitiesOrAssets.getTotalNetAssets()).orElse(0L);
        validateAggregateTotal(totalNetAssets, calculatedTotal, periodPath + OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH, errors);
    }

    private void checkOtherLiabilitiesAreMandatory(BalanceSheet balanceSheet, String periodPath, Errors errors) {

        CurrentAssets currentAssets = balanceSheet.getCurrentAssets();
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = balanceSheet.getOtherLiabilitiesOrAssets();

        if (currentAssets != null ||
                otherLiabilitiesOrAssets.getPrepaymentsAndAccruedIncome() != null ||
                otherLiabilitiesOrAssets.getCreditorsDueWithinOneYear() != null) {

            if (otherLiabilitiesOrAssets.getNetCurrentAssets() == null) {
                addError(errors, mandatoryElementMissing, periodPath + OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH);
            }

            if (otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities() == null) {
                addError(errors, mandatoryElementMissing, periodPath + OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH);
            }
        }
    }

    private void validateTotalShareholderFunds(BalanceSheet balanceSheet, String periodPath, Errors errors) {

        CapitalAndReserves capitalAndReserves = balanceSheet.getCapitalAndReserves();

        if (capitalAndReserves != null) {

            Long calledUpShareCapital = Optional.ofNullable(capitalAndReserves.getCalledUpShareCapital()).orElse(0L);
            Long sharePremiumAccount = Optional.ofNullable(capitalAndReserves.getSharePremiumAccount()).orElse(0L);
            Long otherReserves = Optional.ofNullable(capitalAndReserves.getOtherReserves()).orElse(0L);
            Long profitAndLoss = Optional.ofNullable(capitalAndReserves.getProfitAndLoss()).orElse(0L);

            Long calculatedTotal = calledUpShareCapital + otherReserves + sharePremiumAccount + profitAndLoss;

            Long totalShareholderFunds = Optional.ofNullable(capitalAndReserves.getTotalShareholdersFunds()).orElse(0L);
            validateAggregateTotal(totalShareholderFunds, calculatedTotal, periodPath + TOTAL_SHAREHOLDER_FUNDS_PATH, errors);

            Long totalNetAssets = 0L;
            if (balanceSheet.getOtherLiabilitiesOrAssets() != null) {
                totalNetAssets = balanceSheet.getOtherLiabilitiesOrAssets().getTotalNetAssets();
            }

            if (!totalNetAssets.equals(totalShareholderFunds)) {
                addError(errors, shareholderFundsMismatch, periodPath + TOTAL_SHAREHOLDER_FUNDS_PATH);
            }
        } else {
            addError(errors, mandatoryElementMissing, periodPath + CAPITAL_AND_RESERVES_PATH);
        }
    }

    private void validateTotalMembersFunds(BalanceSheet balanceSheet, String periodPath, Errors errors) {

        MembersFunds membersFunds = balanceSheet.getMembersFunds();

        if (membersFunds != null) {

            Long profitAndLossAccount = membersFunds.getProfitAndLossAccount();

            Long calculatedTotal = profitAndLossAccount;

            Long totalMembersFunds = membersFunds.getTotalMembersFunds();
            validateAggregateTotal(totalMembersFunds, calculatedTotal, periodPath + TOTAL_MEMBERS_FUNDS_PATH, errors);

            Long totalNetAssets = 0L;
            if (balanceSheet.getOtherLiabilitiesOrAssets() != null) {
                totalNetAssets = balanceSheet.getOtherLiabilitiesOrAssets().getTotalNetAssets();
            }

            if (!totalNetAssets.equals(totalMembersFunds)) {
                addError(errors, membersFundsMismatch, periodPath + TOTAL_MEMBERS_FUNDS_PATH);
            }
        } else {
            addError(errors, mandatoryElementMissing, periodPath + MEMBERS_FUNDS_PATH);
        }
    }

    private void validateCalledUpShareCapitalNotPaidNotSubmitted(BalanceSheet balanceSheet, String periodPath, Errors errors) {

        if (balanceSheet.getCalledUpShareCapitalNotPaid() != null) {
            addError(errors, unexpectedData, periodPath + CALLED_UP_SHARE_CAPITAL_NOT_PAID_PATH);
        }
    }

    private void validateCapitalAndReservesNotSubmitted(BalanceSheet balanceSheet, String periodPath, Errors errors) {

        if (balanceSheet.getCapitalAndReserves() != null) {
            addError(errors, unexpectedData, periodPath + CAPITAL_AND_RESERVES_PATH);
        }
    }

    private void validateMembersFundsNotSubmitted(BalanceSheet balanceSheet, String periodPath, Errors errors) {

        if (balanceSheet.getMembersFunds() != null) {
            addError(errors, unexpectedData, periodPath + MEMBERS_FUNDS_PATH);
        }
    }

    private boolean getIsLBGCompanyFiling(Transaction transaction) throws DataException {

        try {
            return companyService.isLBG(transaction);
        } catch (ServiceException e) {
            throw new DataException(e.getMessage(), e);
        }
    }
}
