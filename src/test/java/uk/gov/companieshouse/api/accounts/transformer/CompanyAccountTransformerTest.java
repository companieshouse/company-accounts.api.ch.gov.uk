package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountTransformerTest {

    private static final String ETAG = "etag";

    private static final String KIND = "kind";

    private static final LocalDate NEXT_ACCOUNTS_PERIOD_START_ON = LocalDate.of(2018, 01, 01);

    private static final LocalDate NEXT_ACCOUNTS_PERIOD_END_ON = LocalDate.of(2019, 01, 01);

    private static final LocalDate LAST_ACCOUNTS_PERIOD_START_ON = LocalDate.of(2016, 12, 31);

    private static final LocalDate LAST_ACCOUNTS_PERIOD_END_ON = LocalDate.of(2017, 12, 31);

    private CompanyAccountTransformer companyAccountTransformer = new CompanyAccountTransformer();

    @Test
    @DisplayName("Tests rest to entity account transformer with empty object which should result in null values")
    void testRestToEntityTransformerWithEmptyObject() {

        CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(new CompanyAccount());

        assertNotNull(companyAccountEntity);
        assertNull(companyAccountEntity.getData().getEtag());
        assertNull(companyAccountEntity.getData().getKind());
        assertEquals(new HashMap<>(), companyAccountEntity.getData().getLinks());
        assertNull(companyAccountEntity.getData().getNextAccounts());
        assertNull(companyAccountEntity.getData().getLastAccounts());
    }

    @Test
    @DisplayName("Tests rest to entity account transformer with populated object which has a next accounts")
    void testRestToEntityTransformerWithNextAccounts() {

        CompanyAccount companyAccount = new CompanyAccount();
        companyAccount.setEtag(ETAG);
        companyAccount.setKind(KIND);
        companyAccount.setLinks(new HashMap<>());

        AccountingPeriod nextAccounts = new AccountingPeriod();
        nextAccounts.setPeriodStartOn(NEXT_ACCOUNTS_PERIOD_START_ON);
        nextAccounts.setPeriodEndOn(NEXT_ACCOUNTS_PERIOD_END_ON);
        companyAccount.setNextAccounts(nextAccounts);

        CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(companyAccount);

        assertNotNull(companyAccountEntity);

        CompanyAccountDataEntity companyAccountDataEntity = companyAccountEntity.getData();
        assertNotNull(companyAccountDataEntity);
        assertEquals(ETAG, companyAccountDataEntity.getEtag());
        assertEquals(KIND, companyAccountDataEntity.getKind());
        assertEquals(new HashMap<>(), companyAccountDataEntity.getLinks());

        AccountingPeriodEntity nextAccountsEntity = companyAccountDataEntity.getNextAccounts();
        assertNotNull(nextAccountsEntity);
        assertEquals(NEXT_ACCOUNTS_PERIOD_START_ON, nextAccountsEntity.getPeriodStartOn());
        assertEquals(NEXT_ACCOUNTS_PERIOD_END_ON, nextAccountsEntity.getPeriodEndOn());

        assertNull(companyAccountDataEntity.getLastAccounts());
    }

    @Test
    @DisplayName("Tests rest to entity account transformer with populated object which has a next and last accounts")
    void testRestToEntityTransformerWithNextAndLastAccounts() {

        CompanyAccount companyAccount = new CompanyAccount();
        companyAccount.setEtag(ETAG);
        companyAccount.setKind(ETAG);
        companyAccount.setLinks(new HashMap<>());

        AccountingPeriod nextAccounts = new AccountingPeriod();
        nextAccounts.setPeriodStartOn(NEXT_ACCOUNTS_PERIOD_START_ON);
        nextAccounts.setPeriodEndOn(NEXT_ACCOUNTS_PERIOD_END_ON);
        companyAccount.setNextAccounts(nextAccounts);

        AccountingPeriod lastAccounts = new AccountingPeriod();
        lastAccounts.setPeriodStartOn(LAST_ACCOUNTS_PERIOD_START_ON);
        lastAccounts.setPeriodEndOn(LAST_ACCOUNTS_PERIOD_END_ON);
        companyAccount.setLastAccounts(lastAccounts);

        CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(companyAccount);

        assertNotNull(companyAccountEntity);

        CompanyAccountDataEntity companyAccountDataEntity = companyAccountEntity.getData();
        assertNotNull(companyAccountDataEntity);
        assertEquals(ETAG, companyAccountDataEntity.getEtag());
        assertEquals(ETAG, companyAccountDataEntity.getKind());
        assertEquals(new HashMap<>(), companyAccountDataEntity.getLinks());

        AccountingPeriodEntity nextAccountsEntity = companyAccountDataEntity.getNextAccounts();
        assertNotNull(nextAccountsEntity);
        assertEquals(NEXT_ACCOUNTS_PERIOD_START_ON, nextAccountsEntity.getPeriodStartOn());
        assertEquals(NEXT_ACCOUNTS_PERIOD_END_ON, nextAccountsEntity.getPeriodEndOn());

        AccountingPeriodEntity lastAccountsEntity = companyAccountDataEntity.getLastAccounts();
        assertNotNull(lastAccountsEntity);
        assertEquals(LAST_ACCOUNTS_PERIOD_START_ON, lastAccountsEntity.getPeriodStartOn());
        assertEquals(LAST_ACCOUNTS_PERIOD_END_ON, lastAccountsEntity.getPeriodEndOn());
    }

    @Test
    @DisplayName("Tests entity to rest account transformer with empty object which should result in null values")
    void testEntityToRestTransformerWithEmptyObject() {

        CompanyAccountDataEntity companyAccountDataEntity = new CompanyAccountDataEntity();

        CompanyAccountEntity companyAccountEntity = new CompanyAccountEntity();
        companyAccountEntity.setData(companyAccountDataEntity);

        CompanyAccount companyAccount = companyAccountTransformer.transform(companyAccountEntity);

        assertNotNull(companyAccount);
        assertNull(companyAccount.getEtag());
        assertNull(companyAccount.getKind());
        assertEquals(new HashMap<>(), companyAccount.getLinks());
        assertNull(companyAccount.getNextAccounts());
        assertNull(companyAccount.getLastAccounts());
    }

    @Test
    @DisplayName("Tests entity to rest account transformer with populated object which has a next accounts")
    void testEntityToRestTransformerWithNextAccounts() {

        CompanyAccountDataEntity companyAccountDataEntity = new CompanyAccountDataEntity();
        companyAccountDataEntity.setEtag(ETAG);
        companyAccountDataEntity.setKind(KIND);
        companyAccountDataEntity.setLinks(new HashMap<>());

        AccountingPeriodEntity nextAccountsEntity = new AccountingPeriodEntity();
        nextAccountsEntity.setPeriodStartOn(NEXT_ACCOUNTS_PERIOD_START_ON);
        nextAccountsEntity.setPeriodEndOn(NEXT_ACCOUNTS_PERIOD_END_ON);
        companyAccountDataEntity.setNextAccounts(nextAccountsEntity);

        CompanyAccountEntity companyAccountEntity = new CompanyAccountEntity();
        companyAccountEntity.setData(companyAccountDataEntity);

        CompanyAccount companyAccount = companyAccountTransformer.transform(companyAccountEntity);

        assertNotNull(companyAccount);
        assertEquals(ETAG, companyAccount.getEtag());
        assertEquals(KIND, companyAccount.getKind());
        assertEquals(new HashMap<>(), companyAccount.getLinks());

        AccountingPeriod nextAccounts = companyAccount.getNextAccounts();
        assertNotNull(nextAccounts);
        assertEquals(NEXT_ACCOUNTS_PERIOD_START_ON, nextAccounts.getPeriodStartOn());
        assertEquals(NEXT_ACCOUNTS_PERIOD_END_ON, nextAccounts.getPeriodEndOn());

        assertNull(companyAccount.getLastAccounts());
    }

    @Test
    @DisplayName("Tests entity to rest account transformer with populated object which has a next and last accounts")
    void testEntityToRestTransformerWithNextAndLastAccounts() {

        CompanyAccountDataEntity companyAccountDataEntity = new CompanyAccountDataEntity();
        companyAccountDataEntity.setEtag(ETAG);
        companyAccountDataEntity.setKind(KIND);
        companyAccountDataEntity.setLinks(new HashMap<>());

        AccountingPeriodEntity nextAccountsEntity = new AccountingPeriodEntity();
        nextAccountsEntity.setPeriodStartOn(NEXT_ACCOUNTS_PERIOD_START_ON);
        nextAccountsEntity.setPeriodEndOn(NEXT_ACCOUNTS_PERIOD_END_ON);
        companyAccountDataEntity.setNextAccounts(nextAccountsEntity);

        AccountingPeriodEntity lastAccountsEntity = new AccountingPeriodEntity();
        lastAccountsEntity.setPeriodStartOn(LAST_ACCOUNTS_PERIOD_START_ON);
        lastAccountsEntity.setPeriodEndOn(LAST_ACCOUNTS_PERIOD_END_ON);
        companyAccountDataEntity.setLastAccounts(lastAccountsEntity);

        CompanyAccountEntity companyAccountEntity = new CompanyAccountEntity();
        companyAccountEntity.setData(companyAccountDataEntity);

        CompanyAccount companyAccount = companyAccountTransformer.transform(companyAccountEntity);

        assertNotNull(companyAccount);
        assertEquals(ETAG, companyAccount.getEtag());
        assertEquals(KIND, companyAccount.getKind());
        assertEquals(new HashMap<>(), companyAccount.getLinks());

        AccountingPeriod nextAccounts = companyAccount.getNextAccounts();
        assertNotNull(nextAccounts);
        assertEquals(NEXT_ACCOUNTS_PERIOD_START_ON, nextAccounts.getPeriodStartOn());
        assertEquals(NEXT_ACCOUNTS_PERIOD_END_ON, nextAccounts.getPeriodEndOn());

        AccountingPeriod lastAccounts = companyAccount.getLastAccounts();
        assertNotNull(lastAccounts);
        assertEquals(LAST_ACCOUNTS_PERIOD_START_ON, lastAccounts.getPeriodStartOn());
        assertEquals(LAST_ACCOUNTS_PERIOD_END_ON, lastAccounts.getPeriodEndOn());
    }
}
