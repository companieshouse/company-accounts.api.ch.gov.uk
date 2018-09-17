package uk.gov.companieshouse.api.accounts.transformer;

import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
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
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;


@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodTransformerTest {

    private static final Integer CalledUpShareCapitalNotPaidValid = 5;
    private static final Integer TangibleValid = 10;
    private static final Integer FixedAssetsTotalValid = 10;

    private CurrentPeriodTransformer currentPeriodTransformer = new CurrentPeriodTransformer();

    @Test
    @DisplayName("Tests current period transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        CurrentPeriodEntity companyAccountEntity = currentPeriodTransformer
            .transform(new CurrentPeriod());

        Assertions.assertNotNull(companyAccountEntity);
        Assertions.assertNull(companyAccountEntity.getData().getEtag());
        Assertions.assertEquals(new HashMap<>(), companyAccountEntity.getData().getLinks());

    }

    @Test
    @DisplayName("Tests current period transformer with populated object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {
        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(CalledUpShareCapitalNotPaidValid);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(TangibleValid);
        fixedAssets.setTotalFixedAssets(FixedAssetsTotalValid);

        balanceSheet.setFixedAssets(fixedAssets);

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setEtag("etag");
        currentPeriod.setKind("kind");
        currentPeriod.setLinks(new HashMap<>());
        currentPeriod.setBalanceSheet(balanceSheet);

        CurrentPeriodEntity currentPeriodEntity = currentPeriodTransformer
            .transform(currentPeriod);

        CurrentPeriodDataEntity data = currentPeriodEntity.getData();

        Assertions.assertNotNull(currentPeriodEntity);
        Assertions.assertEquals("etag", data.getEtag());
        Assertions.assertEquals(Integer.valueOf(CalledUpShareCapitalNotPaidValid),
            data.getBalanceSheetEntity()
                .getCalledUpShareCapitalNotPaid());
        Assertions.assertEquals(Integer.valueOf(TangibleValid),
            data.getBalanceSheetEntity().getFixedAssets().getTangible());
        Assertions.assertEquals(Integer.valueOf(FixedAssetsTotalValid),
            data.getBalanceSheetEntity().getFixedAssets().getTotalFixedAssets());
        Assertions.assertEquals("kind", data.getKind());
        Assertions.assertEquals(new HashMap<>(), data.getLinks());
    }

    @Test
    @DisplayName("Tests current period transformer with populated object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {
        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        balanceSheetEntity.setCalledUpShareCapitalNotPaid(CalledUpShareCapitalNotPaidValid);

        FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();
        fixedAssetsEntity.setTangible(TangibleValid);
        fixedAssetsEntity.setTotalFixedAssets(FixedAssetsTotalValid);

        balanceSheetEntity.setFixedAssets(fixedAssetsEntity);

        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        CurrentPeriodDataEntity currentPeriodDataEntity = new CurrentPeriodDataEntity();
        currentPeriodDataEntity.setEtag("etag");
        currentPeriodDataEntity.setKind("kind");
        currentPeriodDataEntity.setLinks(new HashMap<>());
        currentPeriodDataEntity.setBalanceSheetEntity(balanceSheetEntity);
        currentPeriodEntity.setData(currentPeriodDataEntity);

        CurrentPeriod currentPeriod = currentPeriodTransformer
            .transform(currentPeriodEntity);

        Assertions.assertNotNull(currentPeriodEntity);
        Assertions.assertEquals("etag", currentPeriod.getEtag());
        Assertions.assertEquals(Integer.valueOf(CalledUpShareCapitalNotPaidValid),
            currentPeriod.getBalanceSheet()
                .getCalledUpShareCapitalNotPaid());
        Assertions.assertEquals(Integer.valueOf(TangibleValid),
            currentPeriod.getBalanceSheet().getFixedAssets().getTangible());
        Assertions.assertEquals(Integer.valueOf(FixedAssetsTotalValid),
            currentPeriod.getBalanceSheet().getFixedAssets().getTotalFixedAssets());
        Assertions.assertEquals("kind", currentPeriod.getKind());
        Assertions.assertEquals(new HashMap<>(), currentPeriod.getLinks());
    }
}

