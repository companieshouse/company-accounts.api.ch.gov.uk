package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountTransformerTest {

    private CompanyAccountTransformer companyAccountTransformer = new CompanyAccountTransformer();

    @Test
    @DisplayName("Tests account transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(new CompanyAccount());

        Assertions.assertNotNull(companyAccountEntity);
        Assertions.assertNull(companyAccountEntity.getData().getEtag());
        Assertions.assertNull(companyAccountEntity.getData().getKind());
        Assertions.assertEquals(new HashMap<>(), companyAccountEntity.getData().getLinks());
        Assertions.assertNull(companyAccountEntity.getData().getPeriodEndOn());
    }

    @Test
    @DisplayName("Tests account transformer with populated object and validates values returned")
    public void testTransformerWithPopulatedObject() {
        CompanyAccount companyAccount = new CompanyAccount();
        companyAccount.setEtag("etag");
        companyAccount.setKind("kind");
        companyAccount.setLinks(new HashMap<>());
        companyAccount.setPeriodEndOn(LocalDate.of(2018, 1, 1));

        CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(companyAccount);

        Assertions.assertNotNull(companyAccountEntity);
        Assertions.assertEquals("etag", companyAccountEntity.getData().getEtag());
        Assertions.assertEquals(LocalDate.of(2018, 1, 1), companyAccountEntity.getData().getPeriodEndOn());
        Assertions.assertEquals("kind", companyAccountEntity.getData().getKind());
        Assertions.assertEquals(new HashMap<>(), companyAccountEntity.getData().getLinks());
    }
}

