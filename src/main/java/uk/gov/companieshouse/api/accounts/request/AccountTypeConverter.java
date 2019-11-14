package uk.gov.companieshouse.api.accounts.request;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;

@Component
public class AccountTypeConverter extends PropertyEditorSupport {

    private static final Map<String, AccountType> ACCOUNT_TYPE_MAP = new HashMap<>();

    AccountTypeConverter() {

        Arrays.stream(AccountType.values()).forEach(accountType ->
            ACCOUNT_TYPE_MAP.put(accountType.getType(), accountType)
        );
    }

    @Override
    public void setAsText(final String type) {

        AccountType accountType = ACCOUNT_TYPE_MAP.get(type);
        if (accountType == null) {
            throw new RuntimeException();
        }
        setValue(accountType);
    }
}