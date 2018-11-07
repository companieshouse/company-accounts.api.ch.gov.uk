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

import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;


@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodTransformerTest {

    private static final Long CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID = 5L;
    private static final Long TANGIBLE_VALID = 10L;
    private static final Long FIXED_ASSETS_TOTAL_VALID = 10L;

    private static final Long OTHER_LIABILITIES_OR_ASSETS_VALID = 10L;
    private static final Long OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID = 10L;

    private static final Long CURRENT_ASSETS_VALID = 100L;
    private static final Long CURRENT_ASSETS_TOTAL_VALID = 300L;

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

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setEtag("etag");
        currentPeriod.setKind("kind");
        currentPeriod.setLinks(new HashMap<>());
        currentPeriod.setBalanceSheet(balanceSheet);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(TANGIBLE_VALID);
        fixedAssets.setTotal(FIXED_ASSETS_TOTAL_VALID);
        balanceSheet.setFixedAssets(fixedAssets);

        addFixedAssetsToBalanceSheet(balanceSheet);
        addCurrentAssetsToBalanceSheet(balanceSheet);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets =  new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setNetCurrentAssets(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
        currentPeriod.setBalanceSheet(balanceSheet);

        currentPeriod = createCurrentPeriod(balanceSheet);
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

        testEntityAssertsOtherLiabilitiesOrAssetsEntity(data);

        assertEquals("kind", data.getKind());
        assertEquals(new HashMap<>(), data.getLinks());
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

        OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssetsEntity =  new OtherLiabilitiesOrAssetsEntity();
        otherLiabilitiesOrAssetsEntity.setCreditorsDueWithinOneYear(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setNetCurrentAssets(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setPrepaymentsAndAccruedIncome(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setProvisionForLiabilities(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setTotalAssetsLessCurrentLiabilities(OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID);
        balanceSheetEntity.setOtherLiabilitiesOrAssetsEntity(otherLiabilitiesOrAssetsEntity);

        addFixedAssetsToBalanceSheetEntity(balanceSheetEntity);
        addCurrentAssetsToBalanceSheetEntity(balanceSheetEntity);

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
        currentAssetsEntity.setStocks(CURRENT_ASSETS_VALID);
        currentAssetsEntity.setDebtors(CURRENT_ASSETS_VALID);
        currentAssetsEntity.setCashAtBankAndInHand(CURRENT_ASSETS_VALID);
        currentAssetsEntity.setTotal(CURRENT_ASSETS_TOTAL_VALID);

        balanceSheetEntity.setCurrentAssets(currentAssetsEntity);
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

