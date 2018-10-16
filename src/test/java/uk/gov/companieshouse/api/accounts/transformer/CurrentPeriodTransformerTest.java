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
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodTransformerTest {

    private static final Long CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID = 5L;
    private static final Long TANGIBLE_VALID = 10L;
    private static final Long FIXED_ASSETS_TOTAL_VALID = 10L;
    private static final Long VALID_VALUE = 100L;
    private static final Long CURRENT_ASSETS_TOTAL = 300l;

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

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(TANGIBLE_VALID);
        fixedAssets.setTotalFixedAssets(FIXED_ASSETS_TOTAL_VALID);

        balanceSheet.setFixedAssets(fixedAssets);

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(VALID_VALUE);
        currentAssets.setDebtors(VALID_VALUE);
        currentAssets.setCashAtBankAndInHand(VALID_VALUE);
        currentAssets.setTotalCurrentAssets(CURRENT_ASSETS_TOTAL);

        balanceSheet.setCurrentAssets(currentAssets);

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setEtag("etag");
        currentPeriod.setKind("kind");
        currentPeriod.setLinks(new HashMap<>());
        currentPeriod.setBalanceSheet(balanceSheet);

        CurrentPeriodEntity currentPeriodEntity = currentPeriodTransformer.transform(currentPeriod);

        CurrentPeriodDataEntity data = currentPeriodEntity.getData();

        assertNotNull(currentPeriodEntity);
        assertEquals("etag", data.getEtag());
        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID, data.getBalanceSheetEntity().getCalledUpShareCapitalNotPaid());

        assertEquals(TANGIBLE_VALID, data.getBalanceSheetEntity().getFixedAssets().getTangible());
        assertEquals(FIXED_ASSETS_TOTAL_VALID, data.getBalanceSheetEntity().getFixedAssets().getTotalFixedAssets());

        assertEquals(VALID_VALUE, data.getBalanceSheetEntity().getCurrentAssets().getStocks());
        assertEquals(VALID_VALUE, data.getBalanceSheetEntity().getCurrentAssets().getDebtors());
        assertEquals(VALID_VALUE, data.getBalanceSheetEntity().getCurrentAssets().getCashAtBankAndInHand());
        assertEquals(CURRENT_ASSETS_TOTAL, data.getBalanceSheetEntity().getCurrentAssets().getTotalCurrentAssets());

        assertEquals("kind", data.getKind());
        assertEquals(new HashMap<>(), data.getLinks());
    }

    @Test
    @DisplayName("Tests current period transformer with populated object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {
        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        balanceSheetEntity.setCalledUpShareCapitalNotPaid(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID);

        FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();
        fixedAssetsEntity.setTangible(TANGIBLE_VALID);
        fixedAssetsEntity.setTotalFixedAssets(FIXED_ASSETS_TOTAL_VALID);

        balanceSheetEntity.setFixedAssets(fixedAssetsEntity);

        CurrentAssetsEntity currentAssetsEntity = new CurrentAssetsEntity();
        currentAssetsEntity.setStocks(VALID_VALUE);
        currentAssetsEntity.setDebtors(VALID_VALUE);
        currentAssetsEntity.setCashAtBankAndInHand(VALID_VALUE);
        currentAssetsEntity.setTotalCurrentAssets(CURRENT_ASSETS_TOTAL);

        balanceSheetEntity.setCurrentAssets(currentAssetsEntity);

        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        CurrentPeriodDataEntity currentPeriodDataEntity = new CurrentPeriodDataEntity();
        currentPeriodDataEntity.setEtag("etag");
        currentPeriodDataEntity.setKind("kind");
        currentPeriodDataEntity.setLinks(new HashMap<>());
        currentPeriodDataEntity.setBalanceSheetEntity(balanceSheetEntity);
        currentPeriodEntity.setData(currentPeriodDataEntity);

        CurrentPeriod currentPeriod = currentPeriodTransformer.transform(currentPeriodEntity);

        assertNotNull(currentPeriodEntity);
        assertEquals("etag", currentPeriod.getEtag());
        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID, currentPeriod.getBalanceSheet().getCalledUpShareCapitalNotPaid());
        assertEquals(TANGIBLE_VALID, currentPeriod.getBalanceSheet().getFixedAssets().getTangible());
        assertEquals(FIXED_ASSETS_TOTAL_VALID, currentPeriod.getBalanceSheet().getFixedAssets().getTotalFixedAssets());
        assertEquals(VALID_VALUE, currentPeriod.getBalanceSheet().getCurrentAssets().getStocks());
        assertEquals(VALID_VALUE, currentPeriod.getBalanceSheet().getCurrentAssets().getDebtors());
        assertEquals(VALID_VALUE, currentPeriod.getBalanceSheet().getCurrentAssets().getCashAtBankAndInHand());
        assertEquals(CURRENT_ASSETS_TOTAL, currentPeriod.getBalanceSheet().getCurrentAssets().getTotalCurrentAssets());
        assertEquals("kind", currentPeriod.getKind());
        assertEquals(new HashMap<>(), currentPeriod.getLinks());
    }
}

