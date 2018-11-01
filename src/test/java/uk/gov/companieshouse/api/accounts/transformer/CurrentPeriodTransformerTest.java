package uk.gov.companieshouse.api.accounts.transformer;

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
import uk.gov.companieshouse.api.accounts.model.entity.CurrentAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CapitalAndReservesEntity;

import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CapitalAndReserves;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodTransformerTest {

    private static final Long CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID = 5L;
    private static final Long TANGIBLE_VALID = 10L;
    private static final Long FIXED_ASSETS_TOTAL_VALID = 10L;
    private static final Long CURRENT_ASSETS_VALID = 100L;
    private static final Long CURRENT_ASSETS_TOTAL_VALID = 300L;
    private static final Long CALLED_UP_SHARE_CAPITAL = 3L;
    private static final Long OTHER_RESERVES = 6L;
    private static final Long PROFIT_AND_LOSS = 9L;
    private static final Long SHARE_PREMIUM_ACCOUNT = 15L;
    private static final Long TOTAL_SHAREHOLDERS_FUNDS = 45L;

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
        balanceSheet.setCalledUpShareCapitalNotPaid(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID);

        addFixedAssetsToBalanceSheet(balanceSheet);
        addCurrentAssetsToBalanceSheet(balanceSheet);
        addCapitalAndReservesToBalanceSheet(balanceSheet);

        CurrentPeriod currentPeriod = createCurrentPeriod(balanceSheet);
        CurrentPeriodEntity currentPeriodEntity = currentPeriodTransformer.transform(currentPeriod);

        CurrentPeriodDataEntity data = currentPeriodEntity.getData();

        assertNotNull(currentPeriodEntity);
        assertEquals("etag", data.getEtag());
        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID, data.getBalanceSheetEntity().getCalledUpShareCapitalNotPaid());

        assertEquals(TANGIBLE_VALID, data.getBalanceSheetEntity().getFixedAssets().getTangible());
        assertEquals(FIXED_ASSETS_TOTAL_VALID, data.getBalanceSheetEntity().getFixedAssets().getTotal());

        assertEquals(CURRENT_ASSETS_VALID, data.getBalanceSheetEntity().getCurrentAssets().getStocks());
        assertEquals(CURRENT_ASSETS_VALID, data.getBalanceSheetEntity().getCurrentAssets().getDebtors());
        assertEquals(CURRENT_ASSETS_VALID, data.getBalanceSheetEntity().getCurrentAssets().getCashAtBankAndInHand());
        assertEquals(CURRENT_ASSETS_TOTAL_VALID, data.getBalanceSheetEntity().getCurrentAssets().getTotal());

        assertEquals(CALLED_UP_SHARE_CAPITAL, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getCalledUpShareCapital());
        assertEquals(OTHER_RESERVES, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getOtherReserves());
        assertEquals(PROFIT_AND_LOSS, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getProfitAndLoss());
        assertEquals(SHARE_PREMIUM_ACCOUNT, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getSharePremiumAccount());
        assertEquals(TOTAL_SHAREHOLDERS_FUNDS, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getTotal());

        assertEquals("kind", data.getKind());
        assertEquals(new HashMap<>(), data.getLinks());
    }

    private void addCapitalAndReservesToBalanceSheet(BalanceSheet balanceSheet) {
        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(CALLED_UP_SHARE_CAPITAL);
        capitalAndReserves.setOtherReserves(OTHER_RESERVES);
        capitalAndReserves.setProfitAndLoss(PROFIT_AND_LOSS);
        capitalAndReserves.setSharePremiumAccount(SHARE_PREMIUM_ACCOUNT);
        capitalAndReserves.setTotal(TOTAL_SHAREHOLDERS_FUNDS);

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
        currentAssets.setStocks(CURRENT_ASSETS_VALID);
        currentAssets.setDebtors(CURRENT_ASSETS_VALID);
        currentAssets.setCashAtBankAndInHand(CURRENT_ASSETS_VALID);
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

        assertEquals(CURRENT_ASSETS_VALID, currentPeriod.getBalanceSheet().getCurrentAssets().getStocks());
        assertEquals(CURRENT_ASSETS_VALID, currentPeriod.getBalanceSheet().getCurrentAssets().getDebtors());
        assertEquals(CURRENT_ASSETS_VALID, currentPeriod.getBalanceSheet().getCurrentAssets().getCashAtBankAndInHand());
        assertEquals(CURRENT_ASSETS_TOTAL_VALID, currentPeriod.getBalanceSheet().getCurrentAssets().getTotal());

        assertEquals(CALLED_UP_SHARE_CAPITAL, currentPeriod.getBalanceSheet().getCapitalAndReserves().getCalledUpShareCapital());
        assertEquals(OTHER_RESERVES, currentPeriod.getBalanceSheet().getCapitalAndReserves().getOtherReserves());
        assertEquals(PROFIT_AND_LOSS, currentPeriod.getBalanceSheet().getCapitalAndReserves().getProfitAndLoss());
        assertEquals(SHARE_PREMIUM_ACCOUNT, currentPeriod.getBalanceSheet().getCapitalAndReserves().getSharePremiumAccount());
        assertEquals(TOTAL_SHAREHOLDERS_FUNDS, currentPeriod.getBalanceSheet().getCapitalAndReserves().getTotal());

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
        currentAssetsEntity.setStocks(CURRENT_ASSETS_VALID);
        currentAssetsEntity.setDebtors(CURRENT_ASSETS_VALID);
        currentAssetsEntity.setCashAtBankAndInHand(CURRENT_ASSETS_VALID);
        currentAssetsEntity.setTotal(CURRENT_ASSETS_TOTAL_VALID);

        balanceSheetEntity.setCurrentAssets(currentAssetsEntity);
    }

    private void addCapitalAndReservesToBalanceSheetEntity(BalanceSheetEntity balanceSheetEntity) {
        CapitalAndReservesEntity capitalAndReservesEntity = new CapitalAndReservesEntity();
        capitalAndReservesEntity.setCalledUpShareCapital(CALLED_UP_SHARE_CAPITAL);
        capitalAndReservesEntity.setOtherReserves(OTHER_RESERVES);
        capitalAndReservesEntity.setProfitAndLoss(PROFIT_AND_LOSS);
        capitalAndReservesEntity.setSharePremiumAccount(SHARE_PREMIUM_ACCOUNT);
        capitalAndReservesEntity.setTotal(TOTAL_SHAREHOLDERS_FUNDS);

        balanceSheetEntity.setCapitalAndReservesEntity(capitalAndReservesEntity);
    }

    private void addFixedAssetsToBalanceSheetEntity(BalanceSheetEntity balanceSheetEntity) {
        FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();
        fixedAssetsEntity.setTangible(TANGIBLE_VALID);
        fixedAssetsEntity.setTotal(FIXED_ASSETS_TOTAL_VALID);

        balanceSheetEntity.setFixedAssets(fixedAssetsEntity);
    }
}

