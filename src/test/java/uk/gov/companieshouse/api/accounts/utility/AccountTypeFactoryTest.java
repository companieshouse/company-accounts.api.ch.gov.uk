package uk.gov.companieshouse.api.accounts.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;

class AccountTypeFactoryTest {
    private final AccountTypeFactory accountTypeFactory = new AccountTypeFactory();

    private static final String NON_ACCOUNT_LINK_TYPE = "nonAccountLinkType";

    @Test
    void getAccountTypeForSmallFullLinkType() {
        assertEquals(AccountType.SMALL_FULL, accountTypeFactory.getAccountTypeForCompanyAccountLinkType(
                CompanyAccountLinkType.SMALL_FULL.getLink()));
    }

    @Test
    void getAccountTypeForMicroLinkType() {
        assertEquals(AccountType.MICRO, accountTypeFactory.getAccountTypeForCompanyAccountLinkType(
                CompanyAccountLinkType.MICRO.getLink()));
    }

    @Test
    void getAccountTypeForNonAccountLinkType() {
        assertNull(accountTypeFactory.getAccountTypeForCompanyAccountLinkType(NON_ACCOUNT_LINK_TYPE));
    }
}
