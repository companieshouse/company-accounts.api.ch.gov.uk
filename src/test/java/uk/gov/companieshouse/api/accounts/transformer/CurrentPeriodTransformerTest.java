package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.BalanceSheetEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.FixedAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.OtherLiabilitiesOrAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CapitalAndReservesEntity;

import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CapitalAndReserves;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodTransformerTest {

    private static final Long CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID = 5L;
    private static final Long TANGIBLE_VALID = 10L;
    private static final Long FIXED_ASSETS_TOTAL_VALID = 10L;
    private static final Long CASH_AT_BANK_AND_IN_HAND_VALID = 50L;
    private static final Long STOCKS_VALID = 100L;
    private static final Long DEBTORS_VALID = 150L;
    private static final Long CURRENT_ASSETS_TOTAL_VALID = 300L;

    private static final Long OTHER_LIABILITIES_OR_ASSETS_VALID = 10L;
    private static final Long OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID = 10L;

    private static final Long CALLED_UP_SHARE_CAPITAL_VALID = 3L;
    private static final Long OTHER_RESERVES_VALID = 6L;
    private static final Long PROFIT_AND_LOSS_VALID = 9L;
    private static final Long SHARE_PREMIUM_ACCOUNT_VALID = 15L;
    private static final Long TOTAL_SHAREHOLDERS_FUNDS_VALID = 45L;

    private CurrentPeriodTransformer currentPeriodTransformer = new CurrentPeriodTransformer();

    @Test
    @DisplayName("Tests current period transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        CurrentPeriodEntity companyAccountEntity = currentPeriodTransformer
            .transform(new CurrentPeriod());

        assertNotNull(companyAccountEntity);
        assertNull(companyAccountEntity.getData().getEtag());
        assertEquals(new HashMap<>(), companyAccountEntity.getData().getLinks());

    }

    @Test
    @DisplayName("Tests current period transformer with populated object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {

        BalanceSheet balanceSheet = new BalanceSheet();

        CurrentPeriod currentPeriod = createCurrentPeriod(balanceSheet);

        balanceSheet.setCalledUpShareCapitalNotPaid(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID);

        addFixedAssetsToBalanceSheet(balanceSheet);
        addCurrentAssetsToBalanceSheet(balanceSheet);
        addCapitalAndReservesToBalanceSheet(balanceSheet);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets =  new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setNetCurrentAssets(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        currentPeriod.setBalanceSheet(balanceSheet);
        CurrentPeriodEntity currentPeriodEntity = currentPeriodTransformer.transform(currentPeriod);
        CurrentPeriodDataEntity data = currentPeriodEntity.getData();


        assertNotNull(currentPeriodEntity);
        assertEquals("etag", data.getEtag());
        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID, data.getBalanceSheetEntity().getCalledUpShareCapitalNotPaid());

        assertEquals(TANGIBLE_VALID, data.getBalanceSheetEntity().getFixedAssets().getTangible());
        assertEquals(FIXED_ASSETS_TOTAL_VALID, data.getBalanceSheetEntity().getFixedAssets().getTotal());

        assertEquals(STOCKS_VALID, data.getBalanceSheetEntity().getCurrentAssets().getStocks());
        assertEquals(DEBTORS_VALID, data.getBalanceSheetEntity().getCurrentAssets().getDebtors());
        assertEquals(CASH_AT_BANK_AND_IN_HAND_VALID, data.getBalanceSheetEntity().getCurrentAssets().getCashAtBankAndInHand());
        assertEquals(CURRENT_ASSETS_TOTAL_VALID, data.getBalanceSheetEntity().getCurrentAssets().getTotal());

        assertEquals(CALLED_UP_SHARE_CAPITAL_VALID, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getCalledUpShareCapital());
        assertEquals(OTHER_RESERVES_VALID, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getOtherReserves());
        assertEquals(PROFIT_AND_LOSS_VALID, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getProfitAndLoss());
        assertEquals(SHARE_PREMIUM_ACCOUNT_VALID, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getSharePremiumAccount());
        assertEquals(TOTAL_SHAREHOLDERS_FUNDS_VALID, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getTotalShareholdersFund());

        testEntityAssertsOtherLiabilitiesOrAssetsEntity(data);

        assertEquals("kind", data.getKind());
        assertEquals(new HashMap<>(), data.getLinks());
    }

    private void addCapitalAndReservesToBalanceSheet(BalanceSheet balanceSheet) {
        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(CALLED_UP_SHARE_CAPITAL_VALID);
        capitalAndReserves.setOtherReserves(OTHER_RESERVES_VALID);
        capitalAndReserves.setProfitAndLoss(PROFIT_AND_LOSS_VALID);
        capitalAndReserves.setSharePremiumAccount(SHARE_PREMIUM_ACCOUNT_VALID);
        capitalAndReserves.setTotalShareholdersFund(TOTAL_SHAREHOLDERS_FUNDS_VALID);

        balanceSheet.setCapitalAndReserves(capitalAndReserves);
    }


    private CurrentPeriod createCurrentPeriod(BalanceSheet balanceSheet) {
        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setEtag("etag");
        currentPeriod.setKind("kind");
        currentPeriod.setLinks(new HashMap<>());
        currentPeriod.setBalanceSheet(balanceSheet);
        return currentPeriod;
    }

    private void addCurrentAssetsToBalanceSheet(BalanceSheet balanceSheet) {
        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(STOCKS_VALID);
        currentAssets.setDebtors(DEBTORS_VALID);
        currentAssets.setCashAtBankAndInHand(CASH_AT_BANK_AND_IN_HAND_VALID);
        currentAssets.setTotal(CURRENT_ASSETS_TOTAL_VALID);

        balanceSheet.setCurrentAssets(currentAssets);
    }

    private void addFixedAssetsToBalanceSheet(BalanceSheet balanceSheet) {
        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(TANGIBLE_VALID);
        fixedAssets.setTotal(FIXED_ASSETS_TOTAL_VALID);

        balanceSheet.setFixedAssets(fixedAssets);
    }

    @Test
    @DisplayName("Tests current period transformer with populated object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {
        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        balanceSheetEntity.setCalledUpShareCapitalNotPaid(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID);

        OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssetsEntity =  new OtherLiabilitiesOrAssetsEntity();
        otherLiabilitiesOrAssetsEntity.setCreditorsDueWithinOneYear(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setNetCurrentAssets(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setPrepaymentsAndAccruedIncome(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setProvisionForLiabilities(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setTotalAssetsLessCurrentLiabilities(OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID);
        balanceSheetEntity.setOtherLiabilitiesOrAssetsEntity(otherLiabilitiesOrAssetsEntity);

        addFixedAssetsToBalanceSheetEntity(balanceSheetEntity);
        addCurrentAssetsToBalanceSheetEntity(balanceSheetEntity);
        addCapitalAndReservesToBalanceSheetEntity(balanceSheetEntity);

        CurrentPeriodEntity currentPeriodEntity = createCurrentPeriodEntity(balanceSheetEntity);
        CurrentPeriod currentPeriod = currentPeriodTransformer.transform(currentPeriodEntity);

        assertNotNull(currentPeriodEntity);
        assertEquals("etag", currentPeriod.getEtag());
        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID, currentPeriod.getBalanceSheet().getCalledUpShareCapitalNotPaid());

        assertEquals(TANGIBLE_VALID, currentPeriod.getBalanceSheet().getFixedAssets().getTangible());
        assertEquals(FIXED_ASSETS_TOTAL_VALID, currentPeriod.getBalanceSheet().getFixedAssets().getTotal());

        assertEquals(STOCKS_VALID, currentPeriod.getBalanceSheet().getCurrentAssets().getStocks());
        assertEquals(DEBTORS_VALID, currentPeriod.getBalanceSheet().getCurrentAssets().getDebtors());
        assertEquals(CASH_AT_BANK_AND_IN_HAND_VALID, currentPeriod.getBalanceSheet().getCurrentAssets().getCashAtBankAndInHand());
        assertEquals(CURRENT_ASSETS_TOTAL_VALID, currentPeriod.getBalanceSheet().getCurrentAssets().getTotal());

        assertEquals(CALLED_UP_SHARE_CAPITAL_VALID, currentPeriod.getBalanceSheet().getCapitalAndReserves().getCalledUpShareCapital());
        assertEquals(OTHER_RESERVES_VALID, currentPeriod.getBalanceSheet().getCapitalAndReserves().getOtherReserves());
        assertEquals(PROFIT_AND_LOSS_VALID, currentPeriod.getBalanceSheet().getCapitalAndReserves().getProfitAndLoss());
        assertEquals(SHARE_PREMIUM_ACCOUNT_VALID, currentPeriod.getBalanceSheet().getCapitalAndReserves().getSharePremiumAccount());
        assertEquals(TOTAL_SHAREHOLDERS_FUNDS_VALID, currentPeriod.getBalanceSheet().getCapitalAndReserves().getTotalShareholdersFund());

        testRestAssertsOtherLiabilitiesOrAssets(currentPeriod);

        assertEquals("kind", currentPeriod.getKind());
        assertEquals(new HashMap<>(), currentPeriod.getLinks());
    }


    private CurrentPeriodEntity createCurrentPeriodEntity(BalanceSheetEntity balanceSheetEntity) {

        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        CurrentPeriodDataEntity currentPeriodDataEntity = new CurrentPeriodDataEntity();
        currentPeriodDataEntity.setEtag("etag");
        currentPeriodDataEntity.setKind("kind");
        currentPeriodDataEntity.setLinks(new HashMap<>());
        currentPeriodDataEntity.setBalanceSheetEntity(balanceSheetEntity);
        currentPeriodEntity.setData(currentPeriodDataEntity);
        return currentPeriodEntity;
    }

    private void addCurrentAssetsToBalanceSheetEntity(BalanceSheetEntity balanceSheetEntity) {
        CurrentAssetsEntity currentAssetsEntity = new CurrentAssetsEntity();
        currentAssetsEntity.setStocks(STOCKS_VALID);
        currentAssetsEntity.setDebtors(DEBTORS_VALID);
        currentAssetsEntity.setCashAtBankAndInHand(CASH_AT_BANK_AND_IN_HAND_VALID);
        currentAssetsEntity.setTotal(CURRENT_ASSETS_TOTAL_VALID);

        balanceSheetEntity.setCurrentAssets(currentAssetsEntity);
    }

    private void addCapitalAndReservesToBalanceSheetEntity(BalanceSheetEntity balanceSheetEntity) {
        CapitalAndReservesEntity capitalAndReservesEntity = new CapitalAndReservesEntity();
        capitalAndReservesEntity.setCalledUpShareCapital(CALLED_UP_SHARE_CAPITAL_VALID);
        capitalAndReservesEntity.setOtherReserves(OTHER_RESERVES_VALID);
        capitalAndReservesEntity.setProfitAndLoss(PROFIT_AND_LOSS_VALID);
        capitalAndReservesEntity.setSharePremiumAccount(SHARE_PREMIUM_ACCOUNT_VALID);
        capitalAndReservesEntity.setTotalShareholdersFund(TOTAL_SHAREHOLDERS_FUNDS_VALID);

        balanceSheetEntity.setCapitalAndReservesEntity(capitalAndReservesEntity);
    }

    private void addFixedAssetsToBalanceSheetEntity(BalanceSheetEntity balanceSheetEntity) {
        FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();
        fixedAssetsEntity.setTangible(TANGIBLE_VALID);
        fixedAssetsEntity.setTotal(FIXED_ASSETS_TOTAL_VALID);

        balanceSheetEntity.setFixedAssets(fixedAssetsEntity);
    }

    private void testEntityAssertsOtherLiabilitiesOrAssetsEntity(CurrentPeriodDataEntity data) {
        OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssetsEntity = data.getBalanceSheetEntity().getOtherLiabilitiesOrAssetsEntity();
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssetsEntity.getCreditorsDueWithinOneYear());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssetsEntity.getNetCurrentAssets());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssetsEntity.getPrepaymentsAndAccruedIncome());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssetsEntity.getProvisionForLiabilities());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID, otherLiabilitiesOrAssetsEntity.getTotalAssetsLessCurrentLiabilities());
    }

    private void testRestAssertsOtherLiabilitiesOrAssets(CurrentPeriod data) {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = data.getBalanceSheet().getOtherLiabilitiesOrAssets();
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssets.getCreditorsDueWithinOneYear());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssets.getNetCurrentAssets());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssets.getPrepaymentsAndAccruedIncome());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssets.getProvisionForLiabilities());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID, otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities());
    }
}

