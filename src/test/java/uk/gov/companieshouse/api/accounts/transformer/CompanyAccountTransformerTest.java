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

import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CompanyAccountTransformerTest {
    private final CompanyAccountTransformer companyAccountTransformer = new CompanyAccountTransformer();

    @Test
    @DisplayName("Tests rest to entity account transformer with empty object which should result in null values")
    void testRestToEntityTransformerWithEmptyObject() {
        CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(new CompanyAccount());

        assertNotNull(companyAccountEntity);
        assertNull(companyAccountEntity.getData().getEtag());
        assertNull(companyAccountEntity.getData().getKind());
        assertEquals(new HashMap<>(), companyAccountEntity.getData().getLinks());
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
    }
}
