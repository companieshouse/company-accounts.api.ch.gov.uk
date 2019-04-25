package uk.gov.companieshouse.api.accounts.validation;

import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CapitalAndReserves;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class PreviousPeriodValidator extends BaseValidator {

    @Value("${shareholders.mismatch}")
    private String shareholderFundsMismatch;

    private static final String BALANCE_SHEET_PATH = "$.previous_period.balance_sheet";
    private static final String FIXED_ASSETS_PATH = BALANCE_SHEET_PATH + ".fixed_assets";
    private static final String FIXED_ASSETS_TOTAL_PATH = FIXED_ASSETS_PATH + ".total";
    private static final String OTHER_LIABILITIES_OR_ASSETS_PATH = BALANCE_SHEET_PATH +
            ".other_liabilities_or_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH =
            OTHER_LIABILITIES_OR_ASSETS_PATH + ".net_current_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_assets_less_current_liabilities";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH =
            OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_net_assets";
    private static final String CURRENT_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".current_assets" +
            ".total";
    private static final String TOTAL_SHAREHOLDER_FUNDS_PATH = BALANCE_SHEET_PATH +
            ".capital_and_reserves.total_shareholders_funds";


    private CompanyService companyService;

    @Autowired
    public PreviousPeriodValidator(CompanyService companyService){
        this.companyService = companyService;
    }

    public Errors validatePreviousPeriod(@Valid PreviousPeriod previousPeriod, Transaction transaction) throws DataException {

        Errors errors = new Errors();

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        if (isMultipleYearFiler && previousPeriod.getBalanceSheet() != null) {

            validateTotalFixedAssets(previousPeriod, errors);
            validateTotalOtherLiabilitiesOrAssets(previousPeriod, errors);
            validateTotalCurrentAssets(previousPeriod, errors);
            validateTotalShareholderFunds(previousPeriod, errors);

        } else {

            validatePreviousPeriodNotPresent(previousPeriod.getBalanceSheet(), errors);
        }

        return errors;
    }

    private boolean getIsMultipleYearFiler(Transaction transaction) throws DataException {
        try {
            return companyService.isMultipleYearFiler(transaction);
        } catch (ServiceException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    private void validatePreviousPeriodNotPresent(BalanceSheet previousPeriodBalanceSheet,
            Errors errors) {

        if (previousPeriodBalanceSheet != null) {
            addError(errors, unexpectedData, BALANCE_SHEET_PATH);
        }
    }

    private void validateTotalShareholderFunds(PreviousPeriod previousPeriod, Errors errors) {
        CapitalAndReserves capitalAndReserves =
                previousPeriod.getBalanceSheet().getCapitalAndReserves();

        // If any capital and reserves fields are submitted then total shareholder funds cannot
        // be null
        if (capitalAndReserves != null) {

            // Validate calculated total equals total shareholders funds
            Long calledUpShareCapital =
                    Optional.ofNullable(capitalAndReserves.getCalledUpShareCapital()).orElse(0L);
            Long sharePremiumAccount =
                    Optional.ofNullable(capitalAndReserves.getSharePremiumAccount()).orElse(0L);
            Long otherReserves =
                    Optional.ofNullable(capitalAndReserves.getOtherReserves()).orElse(0L);
            Long profitAndLoss =
                    Optional.ofNullable(capitalAndReserves.getProfitAndLoss()).orElse(0L);
            Long totalShareholderFunds =
                    Optional.ofNullable(capitalAndReserves.getTotalShareholdersFunds()).orElse(0L);
            Long calculatedTotal =
                    calledUpShareCapital + otherReserves + sharePremiumAccount + profitAndLoss;
            validateAggregateTotal(totalShareholderFunds, calculatedTotal,
                    TOTAL_SHAREHOLDER_FUNDS_PATH, errors);

            Long totalNetAssets = 0L;
            // Total shareholder funds must equal total net assets
            if (previousPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets() != null) {
                totalNetAssets =
                        previousPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets().getTotalNetAssets();
            }
            if (! totalNetAssets.equals(totalShareholderFunds)) {
                addError(errors, shareholderFundsMismatch, TOTAL_SHAREHOLDER_FUNDS_PATH);
            }
        }
    }

    private void validateTotalCurrentAssets(PreviousPeriod previousPeriod, Errors errors) {

        CurrentAssets currentAssets = previousPeriod.getBalanceSheet().getCurrentAssets();
        if (currentAssets != null) {

            Long stocks = Optional.ofNullable(currentAssets.getStocks()).orElse(0L);
            Long debtors = Optional.ofNullable(currentAssets.getDebtors()).orElse(0L);
            Long cashAtBankAndInHand = Optional.ofNullable(currentAssets.getCashAtBankAndInHand()).orElse(0L);
            Long investments = Optional.ofNullable(currentAssets.getInvestments()).orElse(0L);
            Long currentAssetsTotal = Optional.ofNullable(currentAssets.getTotal()).orElse(0L);

            Long calculatedTotal = stocks + debtors + cashAtBankAndInHand + investments;

            validateAggregateTotal(currentAssetsTotal, calculatedTotal, CURRENT_ASSETS_TOTAL_PATH
                    , errors);
        }
    }

    private void validateTotalFixedAssets(@Valid PreviousPeriod previousPeriod, Errors errors) {

        FixedAssets fixedAssets = previousPeriod.getBalanceSheet().getFixedAssets();
        if (fixedAssets != null) {

            Long tangible = fixedAssets.getTangible();
            Long fixedAssetsTotal = fixedAssets.getTotal();

            // Will calculate the total of all fixedassets fields as they are added to the
            // balance sheet
            Long calculatedTotal = tangible;

            validateAggregateTotal(fixedAssetsTotal, calculatedTotal, FIXED_ASSETS_TOTAL_PATH,
                    errors);
        }
    }

    private void validateTotalOtherLiabilitiesOrAssets(@Valid PreviousPeriod previousPeriod,
            Errors errors) {

        if (previousPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets() != null) {

            calculateOtherLiabilitiesOrAssetsNetCurrentAssets(previousPeriod, errors);
            calculateOtherLiabilitiesOrAssetsTotalAssetsLessCurrentLiabilities(previousPeriod,
                    errors);
            calculateOtherLiabilitiesOrAssetsTotalNetAssets(previousPeriod, errors);
            checkOtherLiabilitiesAreMandatory(previousPeriod, errors);
        }
    }

    private void calculateOtherLiabilitiesOrAssetsNetCurrentAssets(PreviousPeriod previousPeriod,
            Errors errors) {

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets =
                previousPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets();
        Long prepaymentsAndAccruedIncome =
                Optional.ofNullable(otherLiabilitiesOrAssets.getPrepaymentsAndAccruedIncome()).orElse(0L);
        Long creditorsDueWithinOneYear =
                Optional.ofNullable(otherLiabilitiesOrAssets.getCreditorsDueWithinOneYear()).orElse(0L);

        Long totalCurrentAssets = 0L;
        if (previousPeriod.getBalanceSheet().getCurrentAssets() != null) {
            totalCurrentAssets = previousPeriod.getBalanceSheet().getCurrentAssets().getTotal();
        }
        Long calculatedTotal =
                totalCurrentAssets + prepaymentsAndAccruedIncome - creditorsDueWithinOneYear;

        Long netCurrentAssets =
                Optional.ofNullable(otherLiabilitiesOrAssets.getNetCurrentAssets()).orElse(0L);
        validateAggregateTotal(netCurrentAssets, calculatedTotal,
                OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH, errors);
    }

    private void calculateOtherLiabilitiesOrAssetsTotalAssetsLessCurrentLiabilities(PreviousPeriod previousPeriod, Errors errors) {

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets =
                previousPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets();

        Long netCurrentAssets =
                Optional.ofNullable(otherLiabilitiesOrAssets.getNetCurrentAssets()).orElse(0L);

        Long fixedAssetsTotal = 0L;
        if (previousPeriod.getBalanceSheet().getFixedAssets() != null) {
            fixedAssetsTotal =
                    Optional.ofNullable(previousPeriod.getBalanceSheet().getFixedAssets().getTotal()).orElse(0L);
        }

        Long calledUpShareCapital = Optional.ofNullable(previousPeriod.getBalanceSheet().getCalledUpShareCapitalNotPaid()).orElse(0L);

        Long calculatedTotal = fixedAssetsTotal + netCurrentAssets + calledUpShareCapital;

        Long totalAssetsLessCurrentLiabilities =
                Optional.ofNullable(otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities()).orElse(0L);
        validateAggregateTotal(totalAssetsLessCurrentLiabilities, calculatedTotal,
                OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH, errors);
    }

    private void calculateOtherLiabilitiesOrAssetsTotalNetAssets(PreviousPeriod previousPeriod,
            Errors errors) {

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets =
                previousPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets();
        Long totalAssetsLessCurrentLiabilities =
                Optional.ofNullable(otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities()).orElse(0L);
        Long creditorsAfterOneYear =
                Optional.ofNullable(otherLiabilitiesOrAssets.getCreditorsAfterOneYear()).orElse(0L);
        Long accrualsAndDeferredIncome =
                Optional.ofNullable(otherLiabilitiesOrAssets.getAccrualsAndDeferredIncome()).orElse(0L);
        Long provisionForLiabilities =
                Optional.ofNullable(otherLiabilitiesOrAssets.getProvisionForLiabilities()).orElse(0L);

        Long calculatedTotal =
                totalAssetsLessCurrentLiabilities - (creditorsAfterOneYear + accrualsAndDeferredIncome + provisionForLiabilities);

        Long totalNetAssets =
                Optional.ofNullable(otherLiabilitiesOrAssets.getTotalNetAssets()).orElse(0L);
        validateAggregateTotal(totalNetAssets, calculatedTotal,
                OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH, errors);
    }

    private void checkOtherLiabilitiesAreMandatory(PreviousPeriod previousPeriod, Errors errors) {
        CurrentAssets currentAssets = previousPeriod.getBalanceSheet().getCurrentAssets();
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets =
                previousPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets();

        if (currentAssets != null ||
                otherLiabilitiesOrAssets.getPrepaymentsAndAccruedIncome() != null ||
                otherLiabilitiesOrAssets.getCreditorsDueWithinOneYear() != null) {

            if (otherLiabilitiesOrAssets.getNetCurrentAssets() == null) {
                addError(errors, mandatoryElementMissing,
                        OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH);
            }

            if (otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities() == null) {
                addError(errors, mandatoryElementMissing,
                        OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH);
            }
        }
    }
}
