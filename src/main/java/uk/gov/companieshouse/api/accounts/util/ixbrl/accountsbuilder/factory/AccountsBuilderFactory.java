package uk.gov.companieshouse.api.accounts.util.ixbrl.accountsbuilder.factory;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class AccountsBuilderFactory {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String SMALL_FULL_ACCOUNT = "small-full";
    private static final String ABRIDGED_ACCOUNT = "abridged";
    private static final String LOG_ERROR_KEY = "error";
    private static final String LOG_MESSAGE_KEY = "message";

    public AccountsHelper getAccountType(AccountsType accountsType) {

        switch (accountsType.getAccountType()) {
            case SMALL_FULL_ACCOUNT:
                return new SmallFullAccountsHelperImpl();
            default:
                logUnsupportedAccountType(accountsType);
                return null;
        }
    }

    /**
     * Log an error if the account type is unsupported for generating filings
     *
     * @param accountsType
     */
    private void logUnsupportedAccountType(AccountsType accountsType) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(LOG_ERROR_KEY, "Unsupported account type");
        logMap.put(LOG_MESSAGE_KEY, "Account type is unsupported");
        logMap.put("account-type", accountsType);
        LOGGER.error("Unsupported account type", logMap);
    }


}
