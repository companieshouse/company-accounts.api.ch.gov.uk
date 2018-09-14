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
        balanceSheet.setCalledUpShareCapitalNotPaid(5);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(10);
        fixedAssets.setTotalFixedAssets(10);

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
        Assertions.assertEquals(Integer.valueOf(5),
                data.getBalanceSheetEntity()
                        .getCalledUpShareCapitalNotPaid());
        Assertions.assertEquals(Integer.valueOf(10), data.getBalanceSheetEntity().getFixedAssets().getTangible());
        Assertions.assertEquals(Integer.valueOf(10), data.getBalanceSheetEntity().getFixedAssets().getTotalFixedAssets());
        Assertions.assertEquals("kind", data.getKind());
        Assertions.assertEquals(new HashMap<>(), data.getLinks());
    }

    @Test
    @DisplayName("Tests current period transformer with populated object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {
        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        balanceSheetEntity.setCalledUpShareCapitalNotPaid(5);

        FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();
        fixedAssetsEntity.setTangible(10);
        fixedAssetsEntity.setTotalFixedAssets(10);

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
        Assertions.assertEquals(Integer.valueOf(5),
                currentPeriod.getBalanceSheet()
                        .getCalledUpShareCapitalNotPaid());
        Assertions.assertEquals(Integer.valueOf(10), currentPeriod.getBalanceSheet().getFixedAssets().getTangible());
        Assertions.assertEquals(Integer.valueOf(10), currentPeriod.getBalanceSheet().getFixedAssets().getTotalFixedAssets());
        Assertions.assertEquals("kind", currentPeriod.getKind());
        Assertions.assertEquals(new HashMap<>(), currentPeriod.getLinks());
    }
}

