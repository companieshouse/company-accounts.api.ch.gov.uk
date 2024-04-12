package uk.gov.companieshouse.api.accounts.transformer;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.LastAccounts;
import uk.gov.companieshouse.api.accounts.model.rest.NextAccounts;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;

import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class SmallFullTransformerTest {
    
    private final SmallFullTransformer smallFullTransformer = new SmallFullTransformer();
    
    private static final String ETAG = "etag";
    
    private static final String KIND = "kind";

    private static final LocalDate NEXT_ACCOUNTS_PERIOD_START_ON = LocalDate.of(2020, 1, 1);
    private static final LocalDate NEXT_ACCOUNTS_PERIOD_END_ON = LocalDate.of(2020, 12, 31);

    private static final LocalDate LAST_ACCOUNTS_PERIOD_START_ON = LocalDate.of(2019, 1, 1);
    private static final LocalDate LAST_ACCOUNTS_PERIOD_END_ON = LocalDate.of(2019, 12, 31);

    @Test
    @DisplayName("Tests smallfull transformer with empty object which should result in null values")
    void testTransformerWithEmptyObject() {
        SmallFullEntity smallFullEntity = smallFullTransformer.transform(new SmallFull());

        Assertions.assertNotNull(smallFullEntity);
        Assertions.assertNull(smallFullEntity.getData().getEtag());
        Assertions.assertNull(smallFullEntity.getData().getKind());
        Assertions.assertEquals(new HashMap<>(), smallFullEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests smallfull transformer with populated object and validates values returned")
    void testRestToEntityTransformerWithPopulatedObject() {
        SmallFull smallFull = new SmallFull();
        smallFull.setEtag(ETAG);
        smallFull.setKind(KIND);
        smallFull.setLinks(new HashMap<>());

        SmallFullEntity smallFullEntity = smallFullTransformer.transform(smallFull);

        Assertions.assertNotNull(smallFullEntity);
        Assertions.assertEquals(ETAG, smallFullEntity.getData().getEtag());
        Assertions.assertEquals(KIND, smallFullEntity.getData().getKind());
        Assertions.assertEquals(new HashMap<>(), smallFullEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Rest to entity - has next and last accounts")
    void restToEntityHasNextAndLastAccounts() {
        SmallFull smallFull = new SmallFull();
        smallFull.setEtag(ETAG);
        smallFull.setKind(KIND);
        smallFull.setLinks(new HashMap<>());

        NextAccounts nextAccounts = new NextAccounts();
        nextAccounts.setPeriodStartOn(NEXT_ACCOUNTS_PERIOD_START_ON);
        nextAccounts.setPeriodEndOn(NEXT_ACCOUNTS_PERIOD_END_ON);
        smallFull.setNextAccounts(nextAccounts);

        LastAccounts lastAccounts = new LastAccounts();
        lastAccounts.setPeriodStartOn(LAST_ACCOUNTS_PERIOD_START_ON);
        lastAccounts.setPeriodEndOn(LAST_ACCOUNTS_PERIOD_END_ON);
        smallFull.setLastAccounts(lastAccounts);

        SmallFullEntity smallFullEntity = smallFullTransformer.transform(smallFull);

        Assertions.assertNotNull(smallFullEntity);
        Assertions.assertEquals(ETAG, smallFullEntity.getData().getEtag());
        Assertions.assertEquals(KIND, smallFullEntity.getData().getKind());
        Assertions.assertEquals(new HashMap<>(), smallFullEntity.getData().getLinks());
        Assertions.assertNotNull(smallFullEntity.getData().getNextAccounts());
        Assertions.assertEquals(NEXT_ACCOUNTS_PERIOD_START_ON,
                smallFullEntity.getData().getNextAccounts().getPeriodStartOn());
        Assertions.assertEquals(NEXT_ACCOUNTS_PERIOD_END_ON,
                smallFullEntity.getData().getNextAccounts().getPeriodEndOn());
        Assertions.assertNotNull(smallFullEntity.getData().getLastAccounts());
        Assertions.assertEquals(LAST_ACCOUNTS_PERIOD_START_ON,
                smallFullEntity.getData().getLastAccounts().getPeriodStartOn());
        Assertions.assertEquals(LAST_ACCOUNTS_PERIOD_END_ON,
                smallFullEntity.getData().getLastAccounts().getPeriodEndOn());
    }

    @Test
    @DisplayName("Tests smallfull transformer with populated object and validates values returned")
    void testEntityToRestTransformerWithPopulatedObject() {
        SmallFullEntity smallFullEntity = new SmallFullEntity();
        SmallFullDataEntity smallFullDataEntity = new SmallFullDataEntity();
        smallFullDataEntity.setEtag(ETAG);
        smallFullDataEntity.setKind(KIND);
        smallFullDataEntity.setLinks(new HashMap<>());
        smallFullEntity.setData(smallFullDataEntity);

        SmallFull smallFull = smallFullTransformer.transform(smallFullEntity);

        Assertions.assertNotNull(smallFull);
        Assertions.assertEquals(ETAG, smallFull.getEtag());
        Assertions.assertEquals(KIND, smallFull.getKind());
        Assertions.assertEquals(new HashMap<>(), smallFull.getLinks());
    }

    @Test
    @DisplayName("Entity to rest - has next and last accounts")
    void entityToRestHasNextAndLastAccounts() {
        SmallFullEntity smallFullEntity = new SmallFullEntity();
        SmallFullDataEntity smallFullDataEntity = new SmallFullDataEntity();
        smallFullDataEntity.setEtag(ETAG);
        smallFullDataEntity.setKind(KIND);
        smallFullDataEntity.setLinks(new HashMap<>());
        smallFullEntity.setData(smallFullDataEntity);

        AccountingPeriodEntity nextAccounts = new AccountingPeriodEntity();
        nextAccounts.setPeriodStartOn(NEXT_ACCOUNTS_PERIOD_START_ON);
        nextAccounts.setPeriodEndOn(NEXT_ACCOUNTS_PERIOD_END_ON);
        smallFullDataEntity.setNextAccounts(nextAccounts);

        AccountingPeriodEntity lastAccounts = new AccountingPeriodEntity();
        lastAccounts.setPeriodStartOn(LAST_ACCOUNTS_PERIOD_START_ON);
        lastAccounts.setPeriodEndOn(LAST_ACCOUNTS_PERIOD_END_ON);
        smallFullDataEntity.setLastAccounts(lastAccounts);

        SmallFull smallFull = smallFullTransformer.transform(smallFullEntity);

        Assertions.assertNotNull(smallFull);
        Assertions.assertEquals(ETAG, smallFull.getEtag());
        Assertions.assertEquals(KIND, smallFull.getKind());
        Assertions.assertEquals(new HashMap<>(), smallFull.getLinks());
        Assertions.assertNotNull(smallFull.getNextAccounts());
        Assertions.assertEquals(NEXT_ACCOUNTS_PERIOD_START_ON, smallFull.getNextAccounts().getPeriodStartOn());
        Assertions.assertEquals(NEXT_ACCOUNTS_PERIOD_END_ON, smallFull.getNextAccounts().getPeriodEndOn());
        Assertions.assertNotNull(smallFull.getLastAccounts());
        Assertions.assertEquals(LAST_ACCOUNTS_PERIOD_START_ON, smallFull.getLastAccounts().getPeriodStartOn());
        Assertions.assertEquals(LAST_ACCOUNTS_PERIOD_END_ON, smallFull.getLastAccounts().getPeriodEndOn());
    }
}
