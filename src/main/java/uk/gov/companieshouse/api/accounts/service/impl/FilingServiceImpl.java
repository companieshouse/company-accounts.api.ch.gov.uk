package uk.gov.companieshouse.api.accounts.service.impl;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ixbrl.DocumentGeneratorCaller;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class FilingServiceImpl implements FilingService {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String LOG_ACCOUNT_ID_KEY = "account-id";
    private static final String LOG_MESSAGE_KEY = "message";

    private final DocumentGeneratorCaller documentGeneratorCaller;

    @Autowired
    public FilingServiceImpl(
        DocumentGeneratorCaller documentGeneratorCaller) {
        this.documentGeneratorCaller = documentGeneratorCaller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Filing generateAccountFiling(Transaction transaction,
        CompanyAccountEntity companyAccountEntity) {

        AccountsType accountType = getAccountType(companyAccountEntity);
        if (accountType != null) {
            return generateAccountFiling();
        }

        return null;
    }

    /**
     * Get account type by checking the account type link within the account's data.
     *
     * @param accountEntity
     * @return
     */
    private AccountsType getAccountType(CompanyAccountEntity accountEntity) {
        Map<String, String> links = accountEntity.getData().getLinks();

        if (links.containsKey(AccountsType.SMALL_FULL_ACCOUNTS.getResourceKey())) {
            return AccountsType.SMALL_FULL_ACCOUNTS;
        }

        Map<String, Object> logMap = new HashMap<>();
        logMap.put(LOG_MESSAGE_KEY, "Link for account type is missing from account data");
        logMap.put(LOG_ACCOUNT_ID_KEY, accountEntity.getId());
        LOGGER.error("Account Type not found", logMap);

        return null;
    }

    /**
     * Generate the filing for the account type passed in.
     *
     * @return {@link Filing} - null or filing with the filing information (e.g. ixbrl location,
     * accounts name, etc)
     * @throws IOException -
     */
    private Filing generateAccountFiling() {
        if (generateIxbrl() != null) {
            return new Filing();
        }

        return null;
    }

    /**
     * Generates the ixbrl by calling the document generator.
     *
     * @return The location where the service has stored the generated ixbrl.
     */
    private String generateIxbrl() {
        //TODO: call the document generator's new end point to get generate ixbrl. Implementation does NOT exist yet. (STORY SFA-574)
        return documentGeneratorCaller.generateIxbrl();
    }

}