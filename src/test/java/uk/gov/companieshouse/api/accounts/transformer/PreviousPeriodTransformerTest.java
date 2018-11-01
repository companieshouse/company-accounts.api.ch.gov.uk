package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.BalanceSheetEntity;
import uk.gov.companieshouse.api.accounts.model.entity.FixedAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CapitalAndReserves;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodTransformerTest {

    private static final Long CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID = 5L;
    private static final Long TANGIBLE_VALID = 10L;
    private static final Long FIXED_ASSETS_TOTAL_VALID = 10L;
    private static final Long CASH_AT_BANK_AND_IN_HAND_VALID = 50L;
    private static final Long STOCKS_VALID = 100L;
    private static final Long DEBTORS_VALID = 150L;
    private static final Long CURRENT_ASSETS_TOTAL_VALID = 300L;
    private static final Long CALLED_UP_SHARE_CAPITAL = 3L;
    private static final Long OTHER_RESERVES = 6L;
    private static final Long PROFIT_AND_LOSS = 9L;
    private static final Long SHARE_PREMIUM_ACCOUNT = 15L;
    private static final Long TOTAL_SHAREHOLDERS_FUNDS = 45L;

    public static final String ETAG = "etag";
    public static final String KIND = "kind";

    private PreviousPeriodTransformer previousPeriodTransformer = new PreviousPeriodTransformer();

    @Test
    @DisplayName("Tests previous period transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        PreviousPeriodEntity companyAccountEntity = previousPeriodTransformer
            .transform(new PreviousPeriod());

        assertNotNull(companyAccountEntity);
        assertNull(companyAccountEntity.getData().getEtag());
        assertEquals(new HashMap<>(), companyAccountEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests previous period transformer with populated object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID);

        addFixedAssetsToBalanceSheet(balanceSheet);
        addCurrentAssetsToBalanceSheet(balanceSheet);
        addCapitalAndReservesToBalanceSheet(balanceSheet);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setEtag(ETAG);
        previousPeriod.setKind(KIND);
        previousPeriod.setLinks(new HashMap<>());
        previousPeriod.setBalanceSheet(balanceSheet);

        PreviousPeriodEntity previousPeriodEntity = previousPeriodTransformer
            .transform(previousPeriod);

        PreviousPeriodDataEntity data = previousPeriodEntity.getData();

        assertNotNull(previousPeriodEntity);
        assertEquals(ETAG, data.getEtag());
        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID,
            data.getBalanceSheetEntity().getCalledUpShareCapitalNotPaid());

        assertEquals(CASH_AT_BANK_AND_IN_HAND_VALID, data.getBalanceSheetEntity().getCurrentAssets().getCashAtBankAndInHand());
        assertEquals(STOCKS_VALID, data.getBalanceSheetEntity().getCurrentAssets().getStocks());
        assertEquals(DEBTORS_VALID, data.getBalanceSheetEntity().getCurrentAssets().getDebtors());
        assertEquals(CURRENT_ASSETS_TOTAL_VALID, data.getBalanceSheetEntity().getCurrentAssets().getTotalCurrentAssets());

        assertEquals(CALLED_UP_SHARE_CAPITAL, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getCalledUpShareCapital());
        assertEquals(OTHER_RESERVES, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getOtherReserves());
        assertEquals(PROFIT_AND_LOSS, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getProfitAndLoss());
        assertEquals(SHARE_PREMIUM_ACCOUNT, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getSharePremiumAccount());
        assertEquals(TOTAL_SHAREHOLDERS_FUNDS, data.getBalanceSheetEntity().getCapitalAndReservesEntity().getTotal());

        assertEquals(KIND, data.getKind());
        assertEquals(new HashMap<>(), data.getLinks());
    }

    private void addCapitalAndReservesToBalanceSheet(BalanceSheet balanceSheet) {
        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setSharePremiumAccount(SHARE_PREMIUM_ACCOUNT);
        capitalAndReserves.setProfitAndLoss(PROFIT_AND_LOSS);
        capitalAndReserves.setOtherReserves(OTHER_RESERVES);
        capitalAndReserves.setCalledUpShareCapital(CALLED_UP_SHARE_CAPITAL);
        capitalAndReserves.setTotal(TOTAL_SHAREHOLDERS_FUNDS);
        balanceSheet.setCapitalAndReserves(capitalAndReserves);
    }

    private void addCurrentAssetsToBalanceSheet(BalanceSheet balanceSheet) {
        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setCashAtBankAndInHand(CASH_AT_BANK_AND_IN_HAND_VALID);
        currentAssets.setDebtors(DEBTORS_VALID);
        currentAssets.setStocks(STOCKS_VALID);
        currentAssets.setTotalCurrentAssets(CURRENT_ASSETS_TOTAL_VALID);
        balanceSheet.setCurrentAssets(currentAssets);
    }

    private void addFixedAssetsToBalanceSheet(BalanceSheet balanceSheet) {
        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(TANGIBLE_VALID);
        fixedAssets.setTotalFixedAssets(FIXED_ASSETS_TOTAL_VALID);
        balanceSheet.setFixedAssets(fixedAssets);
    }

    @Test
    @DisplayName("ENTITY -> REST - Tests previous period transformer with populated object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {
        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        balanceSheetEntity.setCalledUpShareCapitalNotPaid(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID);

        FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();
        fixedAssetsEntity.setTangible(TANGIBLE_VALID);
        fixedAssetsEntity.setTotalFixedAssets(FIXED_ASSETS_TOTAL_VALID);

        balanceSheetEntity.setFixedAssets(fixedAssetsEntity);

        PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
        PreviousPeriodDataEntity previousPeriodDataEntity = new PreviousPeriodDataEntity();
        previousPeriodDataEntity.setEtag("etag");
        previousPeriodDataEntity.setKind("kind");
        previousPeriodDataEntity.setLinks(new HashMap<>());
        previousPeriodDataEntity.setBalanceSheetEntity(balanceSheetEntity);
        previousPeriodEntity.setData(previousPeriodDataEntity);

        PreviousPeriod previousPeriod = previousPeriodTransformer.transform(previousPeriodEntity);

        assertNotNull(previousPeriodEntity);
        assertEquals("etag", previousPeriod.getEtag());
        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID, previousPeriod.getBalanceSheet().getCalledUpShareCapitalNotPaid());
        assertEquals(TANGIBLE_VALID, previousPeriod.getBalanceSheet().getFixedAssets().getTangible());
        assertEquals(FIXED_ASSETS_TOTAL_VALID, previousPeriod.getBalanceSheet().getFixedAssets().getTotalFixedAssets());
        assertEquals("kind", previousPeriod.getKind());
        assertEquals(new HashMap<>(), previousPeriod.getLinks());
    }
}
