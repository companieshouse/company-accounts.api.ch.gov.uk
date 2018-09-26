package uk.gov.companieshouse.api.accounts.service.impl;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.filing.Data;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.filing.Link;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ixbrl.DocumentGeneratorCaller;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class FilingServiceImpl implements FilingService {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String LOG_ACCOUNT_ID_KEY = "account-id";
    private static final String LOG_MESSAGE_KEY = "message";
    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";
    private static final String LINK_RELATIONSHIP = "accounts";
    private static final String PERIOD_END_ON = "period_end_on";

    private final DocumentGeneratorCaller documentGeneratorCaller;
    private final EnvironmentReader environmentReader;

    @Autowired
    public FilingServiceImpl(DocumentGeneratorCaller documentGeneratorCaller,
        EnvironmentReader environmentReader) {
        this.documentGeneratorCaller = documentGeneratorCaller;
        this.environmentReader = environmentReader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Filing generateAccountFiling(Transaction transaction,
        CompanyAccount companyAccount) {

        AccountsType accountType = getAccountType(companyAccount);
        if (accountType != null) {
            return generateAccountFiling(transaction, accountType);
        }

        return null;
    }

    /**
     * Get account type by checking the account type link within the account's data.
     */
    private AccountsType getAccountType(CompanyAccount companyAccount) {
        Map<String, String> links = companyAccount.getLinks();

        if (links.containsKey(AccountsType.SMALL_FULL_ACCOUNTS.getResourceKey())) {
            return AccountsType.SMALL_FULL_ACCOUNTS;
        }

        Map<String, Object> logMap = new HashMap<>();
        logMap.put(LOG_MESSAGE_KEY, "Link for account type is missing from account data");
        LOGGER.error("Account Type not found", logMap);

        return null;
    }

    /**
     * Generate the filing for the account type passed in.
     *
     * @return {@link Filing} - null or filing with the filing information (e.g. ixbrl location,
     * accounts name, etc)
     */
    private Filing generateAccountFiling(Transaction transaction, AccountsType accountsType) {
        String ixbrlLocation = generateIxbrl();
        if (ixbrlLocation != null && isValidIxbrl()) {
            return createAccountFiling(transaction, accountsType, ixbrlLocation);
        }

        return null;
    }

    /**
     * Generates the ixbrl by calling the document generator.
     *
     * @return The location where the service has stored the generated ixbrl.
     */
    private String generateIxbrl() {
        //TODO: call the document generator's new end point to get generate ixbrl. Implementation does NOT exist yet. (STORY SFA-595)
        return documentGeneratorCaller.generateIxbrl();
    }

    /**
     * Validates the ixbrl against TNEP. This uk.gov.companieshouse.api.accounts.uk.gov.companieshouse.api.accounts.validation
     * is driven by the environment and it can be disable.
     */
    private boolean isValidIxbrl() {
        //TODO: this will be set to true when TNEP Validation plugged in.(STORY SFA-574)
        boolean isIxbrlValid = false;
        if (!environmentReader.getMandatoryBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR)) {
            //TODO Add TNEP uk.gov.companieshouse.api.accounts.uk.gov.companieshouse.api.accounts.validation to be added when functionality is implemented . (STORY SFA-574)
            //isIxbrlValid will be set to false if fails the tnep uk.gov.companieshouse.api.accounts.uk.gov.companieshouse.api.accounts.validation.
            isIxbrlValid = true;
        }

        return isIxbrlValid;
    }

    /**
     * Generates the filing based on the Filing model.
     *
     * @param transaction - transaction information
     * @param accountsType - Account type information: account type, ixbrl's template name, account
     * @param ixbrlLocation - the location where the ixbrl is stored.
     */
    private Filing createAccountFiling(Transaction transaction, AccountsType accountsType,
        String ixbrlLocation) {
        Filing filing = new Filing();

        filing.setCompanyNumber(transaction.getCompanyNumber());
        filing.setDescriptionIdentifier(accountsType.getAccountType());
        filing.setKind(accountsType.getKind());

        //TODO Get Information from document-generator: periodEndDate, descriptionValues. When Implementation is completed (STORY SFA-595)
        LocalDate periodEndDate = LocalDate.now();
        filing.setDescriptionValues(getDescriptionValues(periodEndDate));
        filing.setData(getFilingData(periodEndDate, ixbrlLocation));

        return filing;
    }

    /**
     * Get the description values, which currently contains the period end date. This data is
     * required for the filing description.
     *
     * @param periodEndDate - account's period end date
     * @return {@link Map<String,String>} containing period end date, to match filing model
     */
    private Map<String, String> getDescriptionValues(LocalDate periodEndDate) {
        Map<String, String> descriptionValues = new HashMap<>();
        descriptionValues.put(PERIOD_END_ON, periodEndDate.toString());

        return descriptionValues;
    }

    /**
     * Get the filing data, it contains period end date and the links (ixbrl location and
     * relationship link).
     *
     * @param periodEndDate - accounts end date
     * @param ixbrlLocation - the location where ixbrl is stored
     * @return {@link Data}
     */
    private Data getFilingData(LocalDate periodEndDate, String ixbrlLocation) {
        Data data = new Data();
        data.setPeriodEndOn(periodEndDate);
        data.setLinks(getFilingLinks(ixbrlLocation));

        return data;
    }

    /**
     * Get the Link containing the ixbrl location and the relationship link e.g. accounts.
     *
     * @param ixbrlLocation - the location where ixbrl is stored
     * @return {@link List < Link >}
     */
    private List<Link> getFilingLinks(String ixbrlLocation) {
        Link link = new Link();
        link.setRelationship(LINK_RELATIONSHIP);
        link.setHref(ixbrlLocation);

        return Arrays.asList(link);
    }
}