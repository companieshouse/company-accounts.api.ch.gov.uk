package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountTransformerTest {

    private CompanyAccountTransformer companyAccountTransformer = new CompanyAccountTransformer();

    @Test
    @DisplayName("Tests rest to entity account transformer with empty object which should result in null values")
    void testRestToEntityTransformerWithEmptyObject() {
        CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(new CompanyAccount());

        Assertions.assertNotNull(companyAccountEntity);
        Assertions.assertNull(companyAccountEntity.getData().getEtag());
        Assertions.assertNull(companyAccountEntity.getData().getKind());
        assertEquals(new HashMap<>(), companyAccountEntity.getData().getLinks());
        Assertions.assertNull(companyAccountEntity.getData().getPeriodEndOn());
    }

    @Test
    @DisplayName("Tests rest to entity account transformer with populated object and validates values returned")
    void testRestToEntityTransformerWithPopulatedObject() {
        CompanyAccount companyAccount = new CompanyAccount();
        companyAccount.setEtag("etag");
        companyAccount.setKind("kind");
        companyAccount.setLinks(new HashMap<>());
        companyAccount.setPeriodEndOn(LocalDate.of(2018, 1, 1));

        CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(companyAccount);

        Assertions.assertNotNull(companyAccountEntity);
        assertEquals("etag", companyAccountEntity.getData().getEtag());
        assertEquals(LocalDate.of(2018, 1, 1), companyAccountEntity.getData().getPeriodEndOn());
        assertEquals("kind", companyAccountEntity.getData().getKind());
        assertEquals(new HashMap<>(), companyAccountEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests entity to rest account transformer with populated object and validates values returned")
    void testEntityToRestTransformerWithPopulatedObject() {
        CompanyAccountEntity companyAccountEntity = new CompanyAccountEntity();
        CompanyAccountDataEntity companyAccountDataEntity = new CompanyAccountDataEntity();
        companyAccountDataEntity.setEtag("etag");
        companyAccountDataEntity.setKind("kind");
        companyAccountDataEntity.setLinks(new HashMap<>());
        companyAccountDataEntity.setPeriodEndOn(LocalDate.of(2018, 1, 1));
        companyAccountEntity.setData(companyAccountDataEntity);

        CompanyAccount companyAccount = companyAccountTransformer.transform(companyAccountEntity);

        Assertions.assertNotNull(companyAccount);
        assertEquals("etag", companyAccount.getEtag());
        assertEquals(LocalDate.of(2018, 1, 1), companyAccount.getPeriodEndOn());
        assertEquals("kind", companyAccount.getKind());
        assertEquals(new HashMap<>(), companyAccount.getLinks());
    }
}

