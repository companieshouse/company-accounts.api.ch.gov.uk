package uk.gov.companieshouse.api.accounts.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.filing.Data;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.filing.Link;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.util.DocumentDescriptionHelper;
import uk.gov.companieshouse.api.accounts.util.ixbrl.DocumentGeneratorCaller;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class FilingServiceImpl implements FilingService {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";
    private static final String DOCUMENT_RENDER_SERVICE_END_POINT = "/document-render/store";
    private static final String DOCUMENT_RENDER_SERVICE_HOST_ENV_VAR = "DOCUMENT_RENDER_SERVICE_HOST";
    private static final String LINK_RELATIONSHIP = "accounts";
    private static final String LOG_ACCOUNT_ID_KEY = "account-id";
    private static final String LOG_ERROR_KEY = "error";
    private static final String LOG_MESSAGE_KEY = "message";
    private static final String PERIOD_END_ON = "period_end_on";

    private final EnvironmentReader environmentReader;
    private final DocumentDescriptionHelper documentDescriptionHelper;
    private final DocumentGeneratorCaller documentGeneratorCaller;

    @Autowired
    public FilingServiceImpl(
        EnvironmentReader environmentReader,
        DocumentDescriptionHelper documentDescriptionHelper,
        DocumentGeneratorCaller documentGeneratorCaller) {

        this.environmentReader = environmentReader;
        this.documentDescriptionHelper = documentDescriptionHelper;
        this.documentGeneratorCaller = documentGeneratorCaller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Filing generateAccountFiling(Transaction transaction, CompanyAccountEntity accountEntity)
        throws IOException {

        AccountsType accountType = getAccountType(accountEntity);
        if (accountType != null) {
            return generateAccountFiling(transaction, accountType);
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

        if (links.containsKey(AccountsType.ABRIDGED_ACCOUNTS.getResourceKey())) {
            return AccountsType.ABRIDGED_ACCOUNTS;
        }

        Map<String, Object> logMap = new HashMap<>();
        logMap.put(LOG_ERROR_KEY, "Account Type not found");
        logMap.put(LOG_MESSAGE_KEY, "Link for account type is missing from account data");
        logMap.put(LOG_ACCOUNT_ID_KEY, accountEntity.getId());
        LOGGER.error("Account Type not found", logMap);

        return null;
    }

    /**
     * Generate the filing for the account type passed in.
     *
     * @param transaction - transaction id
     * @param accountsType - Account type information: account type, ixbrl's template name, account
     * @return {@link Filing} - null or filing with the filing information (e.g. ixbrl location,
     * accounts name, etc)
     * @throws IOException -
     */
    private Filing generateAccountFiling(Transaction transaction, AccountsType accountsType)
        throws IOException {
        String ixbrlLocation = generateIxbrl();

        if (ixbrlLocation != null && isValidIxbrl()) {
            return createAccountFiling(transaction, accountsType, ixbrlLocation);
        }

        return null;
    }

    /**
     * Generates the filing based on the Filing model.
     *
     * @param transaction - transaction information
     * @param accountsType - Account type information: account type, ixbrl's template name, account
     * @param ixbrlLocation - the location where the ixbrl is stored.
     * @return
     * @throws IOException
     */
    private Filing createAccountFiling(Transaction transaction, AccountsType accountsType,
        String ixbrlLocation) throws IOException {
        Filing filing = new Filing();

        //TODO get correct periodEndOn. periodEndOn = Current Period's end date", mongo DB. Waiting for Doc.Gen changes. (STORY SFA-574)
        LocalDate periodEndDate = LocalDate.now();

        filing.setCompanyNumber(transaction.getCompanyNumber());
        filing.setDescriptionIdentifier(accountsType.getAccountType());
        filing.setKind(accountsType.getKind());
        filing.setDescription(getFilingDescription(accountsType, periodEndDate));
        filing.setDescriptionValues(getDescriptionValues(periodEndDate));
        filing.setData(getFilingData(periodEndDate, ixbrlLocation));

        return filing;
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
     * @return {@link List<Link>}
     */
    private List<Link> getFilingLinks(String ixbrlLocation) {
        Link link = new Link();
        link.setRelationship(LINK_RELATIONSHIP);
        link.setHref(ixbrlLocation);

        return Arrays.asList(link);
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
     * Get the description for the filing. The description for the account type is retrieved by
     * using DocumentDescriptionHelper class.
     *
     * @param accountsType - Account type information: account type, ixbrl's template name, account
     * @param periodEndDate - account's period end date
     * @return accounts description.
     * @throws IOException
     */
    private String getFilingDescription(AccountsType accountsType, LocalDate periodEndDate)
        throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PERIOD_END_ON, periodEndDate);

        return documentDescriptionHelper
            .getDescription(accountsType.getFilingDescriptionKey(), parameters);
    }

    /**
     * Validates the ixbrl against TNEP. This validation is driven by the env. variable and it can
     * be disable.
     */
    private boolean isValidIxbrl() {

        boolean isIxbrlValid = true;
        if (environmentReader.getMandatoryBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR)) {
            //TODO TNEP validation needs to be added. Copy logic from abridged. (STORY SFA-574)
        }

        return isIxbrlValid;
    }

    /**
     * Generates the ixbrl by calling the document generator passing the the end point and
     * company/account id.
     *
     * @return The location where the service has stored the generated ixbrl.
     */
    private String generateIxbrl() {
        //TODO: call the document generator's new end point to get generate ixbrl. Implementation does NOT exist yet. (STORY SFA-574)
        return  documentGeneratorCaller.generateIxbrl(getServiceURL());
    }

    /**
     * Build the document render service URL.
     *
     * @return the document render service end point e.g. "http://chs-dev:4082/document-render/store"
     */
    private String getServiceURL() {
        return getMandatoryEnvVariable(DOCUMENT_RENDER_SERVICE_HOST_ENV_VAR) +
            DOCUMENT_RENDER_SERVICE_END_POINT;
    }

    /**
     * Get environment variable value for the passed variable.
     *
     * @return environment variable value.
     */
    private String getMandatoryEnvVariable(String envVariable) {
        return environmentReader.getMandatoryString(envVariable);
    }
}
