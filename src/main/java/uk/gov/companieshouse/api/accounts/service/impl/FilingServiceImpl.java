package uk.gov.companieshouse.api.accounts.service.impl;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.filing.Data;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.filing.Link;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.DocumentGeneratorResponse;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.Links;
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
            return generateAccountFiling(transaction, companyAccount, accountType);
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
    private Filing generateAccountFiling(Transaction transaction, CompanyAccount companyAccount,
        AccountsType accountsType) {

        DocumentGeneratorResponse documentGeneratorResponse =
            getDocumentGeneratorResponse(transaction, companyAccount);

        if (documentGeneratorResponse != null) {
            String ixbrlLocation = getIxbrlLocation(documentGeneratorResponse);
            if (ixbrlLocation != null && isValidIxbrl()) {
                return createAccountFiling(transaction, accountsType, documentGeneratorResponse);
            }
        }

        return null;
    }

    /**
     * Calls the document generator to obtain the information needed to build the filing object:
     * e.g. ixbrl location, description and period end date.
     *
     * @return The location where the service has stored the generated ixbrl.
     */
    private DocumentGeneratorResponse getDocumentGeneratorResponse(Transaction transaction,
        CompanyAccount companyAccount) {

        String companyAccountsURI = companyAccount.getLinks()
            .get(CompanyAccountLinkType.SELF.getLink());

        //TODO: Check information from document generator's response is correct. Implementation does NOT exist yet. (STORY SFA-595)
        DocumentGeneratorResponse documentGeneratorResponse =
            documentGeneratorCaller
                .callDocumentGeneratorService(transaction.getId(), companyAccountsURI);

        if (documentGeneratorResponse != null) {
            return documentGeneratorResponse;
        }

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("company accounts uri", companyAccountsURI);
        logMap.put("transaction id", transaction.getId());
        logMap.put(LOG_MESSAGE_KEY, "Document Generator caller has failed. Response is null");

        LOGGER.error("FilingServiceImpl: Document Generator call failed", logMap);

        return null;
    }

    /**
     * Get the ixbrl location from the documentGeneratorResponse if exists.
     *
     * @param documentGeneratorResponse document generator response information
     * @return the ixbrl location or null if not set.
     */
    private String getIxbrlLocation(
        DocumentGeneratorResponse documentGeneratorResponse) {

        String ixbrlLocation = Optional.of(documentGeneratorResponse)
            .map(DocumentGeneratorResponse::getLinks)
            .map(Links::getLocation)
            .orElse(null);

        if (ixbrlLocation != null) {
            return ixbrlLocation;
        }

        Map<String, Object> logMap = new HashMap<>();
        logMap.put(LOG_MESSAGE_KEY,
            "Ixbrl location does not exist in the Document Generator Response");
        LOGGER.error("FilingServiceImpl: Ixbrl is not set", logMap);

        return null;
    }

    /**
     * Validates the ixbrl against TNEP. This validation is driven by the environment and it can be
     * disable.
     */
    private boolean isValidIxbrl() {
        //TODO: this will be set to true when TNEP Validation plugged in.(STORY SFA-574)
        boolean isIxbrlValid = false;
        if (!environmentReader.getMandatoryBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR)) {
            //TODO Add TNEP validation to be added when functionality is implemented . (STORY SFA-574)
            //isIxbrlValid will be set to false if fails the tnep validation.
            isIxbrlValid = true;
        }

        return isIxbrlValid;
    }

    /**
     * Generates the filing based on the Filing model.
     *
     * @param transaction - transaction information
     * @param accountsType - Account type information: account type, ixbrl's template name, account
     * @param documentGeneratorResponse - the location where the ixbrl is stored.
     */
    private Filing createAccountFiling(Transaction transaction, AccountsType accountsType,
        DocumentGeneratorResponse documentGeneratorResponse) {

        Filing filing = new Filing();

        filing.setCompanyNumber(transaction.getCompanyNumber());
        filing.setDescriptionIdentifier(accountsType.getAccountType());
        filing.setKind(accountsType.getKind());

        //TODO Check if documentGeneratorResponse contains correct information: periodEndDate, descriptionValues, description. When Implementation is completed (STORY SFA-595)
        filing.setDescriptionValues(documentGeneratorResponse.getDescriptionValues());
        filing.setDescription(documentGeneratorResponse.getDescription());
        filing.setData(
            createFilingData(getPeriodEndDate(documentGeneratorResponse),
                documentGeneratorResponse.getLinks().getLocation()));

        return filing;
    }

    /**
     * Retrieve the period end date from the document generator response, and set the date in the
     * expected format YYYYMMDD.
     *
     * @param documentGeneratorResponse
     * @return
     */
    private LocalDate getPeriodEndDate(
        DocumentGeneratorResponse documentGeneratorResponse) {

        //TODO check the periodEndDate is being formatted to: YYYY MM DD.
        if (documentGeneratorResponse.getDescriptionValues().containsKey(PERIOD_END_ON)) {
            return LocalDate
                .parse(documentGeneratorResponse.getDescriptionValues().get(PERIOD_END_ON));
        }
        return null;
    }

    /**
     * Get the description values, which currently contains the period end date. This data is
     * required for the filing description.
     *
     * @param periodEndDate - account's period end date
     * @return {@link Map<String,String>} containing period end date, to match filing model
     */
    private Map<String, String> getDescriptionValues(LocalDate periodEndDate) {
        //TODO remove this method if no longer needed. it was used by filing.setDescriptionValues()
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
    private Data createFilingData(LocalDate periodEndDate, String ixbrlLocation) {
        Data data = new Data();
        data.setPeriodEndOn(periodEndDate);
        data.setLinks(createFilingLinks(ixbrlLocation));

        return data;
    }

    /**
     * Get the Link containing the ixbrl location and the relationship link e.g. accounts.
     *
     * @param ixbrlLocation - the location where ixbrl is stored
     * @return {@link List < Link >}
     */
    private List<Link> createFilingLinks(String ixbrlLocation) {
        Link link = new Link();
        link.setRelationship(LINK_RELATIONSHIP);
        link.setHref(ixbrlLocation);

        return Arrays.asList(link);
    }
}