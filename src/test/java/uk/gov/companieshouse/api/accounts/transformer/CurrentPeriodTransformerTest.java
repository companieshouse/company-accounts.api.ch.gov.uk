package uk.gov.companieshouse.api.accounts.transformer;

import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;


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
    public void testTransformerWithPopulatedObject() {
        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(5);

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setEtag("etag");
        currentPeriod.setKind("kind");
        currentPeriod.setLinks(new HashMap<>());
        currentPeriod.setBalanceSheet(balanceSheet);

        CurrentPeriodEntity currentPeriodEntity = currentPeriodTransformer
                .transform(currentPeriod);

        Assertions.assertNotNull(currentPeriodEntity);
        Assertions.assertEquals("etag", currentPeriodEntity.getData().getEtag());
        Assertions.assertEquals(Integer.valueOf(5),
                currentPeriodEntity.getData().getBalanceSheetEntity()
                        .getCalledUpShareCapitalNotPaid());
        Assertions.assertEquals(new HashMap<>(), currentPeriodEntity.getData().getLinks());
    }
}

