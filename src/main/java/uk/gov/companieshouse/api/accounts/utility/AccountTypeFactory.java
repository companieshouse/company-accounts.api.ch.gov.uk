package uk.gov.companieshouse.api.accounts.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;

@Component
public class AccountTypeFactory {

    private final Map<String, AccountType> accountTypeMap;

    AccountTypeFactory() {
        this.accountTypeMap = new HashMap<>();

        Arrays.stream(AccountType.values()).forEach(accountType ->
                this.accountTypeMap.put(accountType.getCompanyAccountLinkType().getLink(), accountType));
    }

    public AccountType getAccountTypeForCompanyAccountLinkType(String companyAccountLinkType) {
        return accountTypeMap.get(companyAccountLinkType);
    }
}
