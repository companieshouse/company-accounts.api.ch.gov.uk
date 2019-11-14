package uk.gov.companieshouse.api.accounts.request;

import java.util.Arrays;
import java.util.EnumMap;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.enumeration.Period;
import uk.gov.companieshouse.api.accounts.enumeration.Resource;

@Component
public class AccountsResourceConverter {

    private static final EnumMap<AccountType, EnumMap<Resource, AccountsResource>>
            ACCOUNTS_RESOURCE_PERIOD_AGNOSTIC_MAP = new EnumMap<>(AccountType.class);

    private static final EnumMap<AccountType, EnumMap<Resource, EnumMap<Period, AccountsResource>>>
            ACCOUNTS_RESOURCE_PERIOD_CONCERNED_MAP = new EnumMap<>(AccountType.class);

    AccountsResourceConverter() {

        Arrays.stream(AccountsResource.values())
                .filter(accountsResource -> accountsResource.getPeriod() == null)
                .forEach(accountsResource -> {

                    if (ACCOUNTS_RESOURCE_PERIOD_AGNOSTIC_MAP.get(accountsResource.getAccountType()) == null) {
                        ACCOUNTS_RESOURCE_PERIOD_AGNOSTIC_MAP.put(accountsResource.getAccountType(), new EnumMap<>(Resource.class));
                    }

                    ACCOUNTS_RESOURCE_PERIOD_AGNOSTIC_MAP
                            .get(accountsResource.getAccountType())
                                    .put(accountsResource.getResource(), accountsResource);
                });

        Arrays.stream(AccountsResource.values())
                .filter(accountsResource -> accountsResource.getPeriod() != null)
                .forEach(accountsResource -> {

                    if (ACCOUNTS_RESOURCE_PERIOD_CONCERNED_MAP.get(accountsResource.getAccountType()) == null) {
                        ACCOUNTS_RESOURCE_PERIOD_CONCERNED_MAP.put(accountsResource.getAccountType(), new EnumMap<>(Resource.class));
                    }

                    if (ACCOUNTS_RESOURCE_PERIOD_CONCERNED_MAP.get(accountsResource.getAccountType()).get(accountsResource.getResource()) == null) {
                        ACCOUNTS_RESOURCE_PERIOD_CONCERNED_MAP.get(accountsResource.getAccountType()).put(accountsResource.getResource(), new EnumMap<>(Period.class));
                    }

                    ACCOUNTS_RESOURCE_PERIOD_CONCERNED_MAP
                            .get(accountsResource.getAccountType())
                                    .get(accountsResource.getResource())
                                            .put(accountsResource.getPeriod(), accountsResource);
                });
    }

    public AccountsResource getAccountsResource(AccountType accountType, Resource resource, Period period) {

        if (period == null) {

            AccountsResource accountsResource = ACCOUNTS_RESOURCE_PERIOD_AGNOSTIC_MAP.get(accountType).get(resource);
            if (accountsResource == null) {
                throw new RuntimeException();
            }
            return accountsResource;
        } else {

            AccountsResource accountsResource = ACCOUNTS_RESOURCE_PERIOD_CONCERNED_MAP.get(accountType).get(resource).get(period);
            if (accountsResource == null) {
                throw new RuntimeException();
            }
            return accountsResource;
        }
    }
}
