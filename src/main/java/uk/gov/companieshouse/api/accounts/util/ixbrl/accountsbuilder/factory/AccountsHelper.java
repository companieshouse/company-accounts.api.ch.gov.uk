package uk.gov.companieshouse.api.accounts.util.ixbrl.accountsbuilder.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.security.NoSuchAlgorithmException;

public interface AccountsHelper<T1, T2> {

    T1 getAccountTypeInformation(String accountId) throws NoSuchAlgorithmException;

    T2 buildAccount(T1 account);

    String getAccountsJsonFormat(String accountId)
        throws NoSuchAlgorithmException, JsonProcessingException;
}
