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
import uk.gov.companieshouse.api.accounts.model.entity.AccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Account;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class AccountTransformerTest {

    private AccountTransformer accountTransformer = new AccountTransformer();

    @Test
    @DisplayName("Tests account transformer with null values which should throw an exception")
    public void testTransformerWithNull() {
        assertThrows(IllegalArgumentException.class, () -> accountTransformer.transform(null));
    }

    @Test
    @DisplayName("Tests account transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        AccountEntity accountEntity = accountTransformer.transform(new Account());

        Assertions.assertNotNull(accountEntity);
        Assertions.assertNull(accountEntity.getData().getEtag());
        Assertions.assertNull(accountEntity.getData().getKind());
        Assertions.assertEquals(new HashMap<>(), accountEntity.getData().getLinks());
        Assertions.assertNull(accountEntity.getData().getPeriodEndOn());
    }

    @Test
    @DisplayName("Tests account transformer with populated object and validates values returned")
    public void testTransformerWithPopulatedObject() {
        Account account = new Account();
        account.setEtag("etag");
        account.setKind("kind");
        account.setLinks(new HashMap<>());
        account.setPeriodEndOn(LocalDate.of(20181, 1, 1));

        AccountEntity accountEntity = accountTransformer.transform(account);

        Assertions.assertNotNull(accountEntity);
        Assertions.assertEquals("etag", accountEntity.getData().getEtag());
        Assertions.assertEquals("kind", accountEntity.getData().getKind());
        Assertions.assertEquals(new HashMap<>(), accountEntity.getData().getLinks());
    }
}

