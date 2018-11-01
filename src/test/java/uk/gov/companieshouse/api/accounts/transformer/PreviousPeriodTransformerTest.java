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
import uk.gov.companieshouse.api.accounts.model.entity.OtherLiabilitiesOrAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodTransformerTest {

    private static final Long CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID = 5L;
    private static final Long TANGIBLE_VALID = 10L;
    private static final Long FIXED_ASSETS_TOTAL_VALID = 10L;
    private static final Long OTHER_LIABILITIES_OR_ASSETS_VALID = 10L;
    private static final Long OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID = 10L;

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

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setEtag(ETAG);
        previousPeriod.setKind(KIND);
        previousPeriod.setLinks(new HashMap<>());

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(TANGIBLE_VALID);
        fixedAssets.setTotal(FIXED_ASSETS_TOTAL_VALID);
        balanceSheet.setFixedAssets(fixedAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets =  new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setNetCurrentAssets(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
        previousPeriod.setBalanceSheet(balanceSheet);

        PreviousPeriodEntity previousPeriodEntity = previousPeriodTransformer
            .transform(previousPeriod);

        PreviousPeriodDataEntity data = previousPeriodEntity.getData();

        assertNotNull(previousPeriodEntity);
        assertEquals(ETAG, data.getEtag());
        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID, data.getBalanceSheetEntity().getCalledUpShareCapitalNotPaid());
        assertEquals(TANGIBLE_VALID, data.getBalanceSheetEntity().getFixedAssets().getTangible());
        assertEquals(FIXED_ASSETS_TOTAL_VALID, data.getBalanceSheetEntity().getFixedAssets().getTotal());

        testEntityAssertsOtherLiabilitiesOrAssetsEntity(data);
        assertEquals(KIND, data.getKind());
        assertEquals(new HashMap<>(), data.getLinks());
    }

    @Test
    @DisplayName("ENTITY -> REST - Tests previous period transformer with populated object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {
        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        balanceSheetEntity.setCalledUpShareCapitalNotPaid(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID);

        FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();
        fixedAssetsEntity.setTangible(TANGIBLE_VALID);
        fixedAssetsEntity.setTotal(FIXED_ASSETS_TOTAL_VALID);

        balanceSheetEntity.setFixedAssets(fixedAssetsEntity);

        OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssetsEntity =  new OtherLiabilitiesOrAssetsEntity();
        otherLiabilitiesOrAssetsEntity.setCreditorsDueWithinOneYear(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setNetCurrentAssets(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setPrepaymentsAndAccruedIncome(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setProvisionForLiabilities(OTHER_LIABILITIES_OR_ASSETS_VALID);
        otherLiabilitiesOrAssetsEntity.setTotalAssetsLessCurrentLiabilities(OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID);
        balanceSheetEntity.setOtherLiabilitiesOrAssetsEntity(otherLiabilitiesOrAssetsEntity);

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

        assertEquals(FIXED_ASSETS_TOTAL_VALID, previousPeriod.getBalanceSheet().getFixedAssets().getTotal());

        testRestAssertsOtherLiabilitiesOrAssets(previousPeriod);

        assertEquals("kind", previousPeriod.getKind());
        assertEquals(new HashMap<>(), previousPeriod.getLinks());
    }

    private void testEntityAssertsOtherLiabilitiesOrAssetsEntity(PreviousPeriodDataEntity data) {
        OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssetsEntity = data.getBalanceSheetEntity().getOtherLiabilitiesOrAssetsEntity();
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssetsEntity.getCreditorsDueWithinOneYear());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssetsEntity.getNetCurrentAssets());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssetsEntity.getPrepaymentsAndAccruedIncome());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssetsEntity.getProvisionForLiabilities());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID, otherLiabilitiesOrAssetsEntity.getTotalAssetsLessCurrentLiabilities());
    }

    private void testRestAssertsOtherLiabilitiesOrAssets(PreviousPeriod data) {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = data.getBalanceSheet().getOtherLiabilitiesOrAssets();
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssets.getCreditorsDueWithinOneYear());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssets.getNetCurrentAssets());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssets.getPrepaymentsAndAccruedIncome());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_VALID, otherLiabilitiesOrAssets.getProvisionForLiabilities());
        assertEquals(OTHER_LIABILITIES_OR_ASSETS_TOTAL_VALID, otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities());
    }
}
