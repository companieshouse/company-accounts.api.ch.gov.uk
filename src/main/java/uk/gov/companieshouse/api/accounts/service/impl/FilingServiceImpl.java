package uk.gov.companieshouse.api.accounts.service.impl;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
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
    private static final String LOG_DOC_GENERATOR_RESPONSE_INVALID_MESSAGE_KEY =
        "FilingServiceImpl: Document generator response invalid";

    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";
    private static final String LINK_RELATIONSHIP = "accounts";
    private static final String PERIOD_END_ON = "period_end_on";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
        .ofPattern("yyyy-MM-dd");

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

        if (documentGeneratorResponse != null &&
            isDocumentGeneratorResponseValid(documentGeneratorResponse) &&
            isValidIxbrl()) {

            return createAccountFiling(transaction, accountsType, documentGeneratorResponse);
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

        DocumentGeneratorResponse documentGeneratorResponse =
            documentGeneratorCaller
                .callDocumentGeneratorService(transaction.getId(), companyAccountsURI);

        if (documentGeneratorResponse != null) {
            return documentGeneratorResponse;
        }

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("company_account_self_link", companyAccountsURI);
        logMap.put("transaction_id", transaction.getId());
        logMap.put(LOG_MESSAGE_KEY, "Document Generator caller has failed. Response is null");

        LOGGER.error("FilingServiceImpl: Document Generator call failed", logMap);

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
        filing.setDescriptionValues(documentGeneratorResponse.getDescriptionValues());
        filing.setDescription(documentGeneratorResponse.getDescription());
        filing.setData(createFilingData(documentGeneratorResponse));

        return filing;
    }

    /**
     * Retrieve the period end date from the document generator response, and set the date in the
     * expected format YYYY-mm-dd.
     *
     * @param documentGeneratorResponse information from the document generator call.
     * @return period end dated formatted
     */
    private LocalDate getPeriodEndDateFormatted(
        DocumentGeneratorResponse documentGeneratorResponse) {

        return LocalDate.parse(documentGeneratorResponse.getDescriptionValues().get(PERIOD_END_ON),
            DATE_TIME_FORMATTER);
    }

    /**
     * Get the filing data, it contains period end date and the links (ixbrl location and
     * relationship link).
     *
     * @return {@link Data}
     */
    private Data createFilingData(DocumentGeneratorResponse response) {
        Data data = new Data();
        data.setPeriodEndOn(getPeriodEndDateFormatted(response));
        data.setLinks(createFilingLinks(response.getLinks().getLocation()));

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

    /**
     * Check if the document generator response contains the information needed to build the filing
     * model: Description, period end on (within description values) and the ixbrl location are
     * needed.
     *
     * @param response Contains the document generator response.
     * @return true if the response contains all the needed information.
     */
    private boolean isDocumentGeneratorResponseValid(DocumentGeneratorResponse response) {
        boolean isDocGeneratorResponseValid = true;

        if (!isPeriodEndOnInDocGeneratorResponse(response)) {
            isDocGeneratorResponseValid = false;
        }

        if (!isDescriptionInDocGeneratorResponse(response)) {
            isDocGeneratorResponseValid = false;
        }

        if (!isIxbrlInDocGeneratorResponse(response)) {
            isDocGeneratorResponseValid = false;
        }

        return isDocGeneratorResponseValid;
    }

    /**
     * Checks if the document generator response contains the ixbrl location.
     *
     * @param response Contains the document generator response.
     * @return true when ixbrl location is not null or blank.
     */
    private boolean isIxbrlInDocGeneratorResponse(DocumentGeneratorResponse response) {

        if (StringUtils.isBlank(getIxbrlLocationFromDocGeneratorResponse(response))) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put(LOG_MESSAGE_KEY,
                "The Ixbrl location has not been set in Document Generator Response");

            LOGGER.error(LOG_DOC_GENERATOR_RESPONSE_INVALID_MESSAGE_KEY, logMap);

            return false;
        }
        return true;
    }

    /**
     * Checks if the document generator response contains account's description.
     *
     * @param response Contains the document generator response.
     * @return true when the description not null or blank.
     */
    private boolean isDescriptionInDocGeneratorResponse(DocumentGeneratorResponse response) {

        if (StringUtils.isBlank(response.getDescription())) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put(LOG_MESSAGE_KEY,
                "The description has not been in the Document Generator Response");
            LOGGER.error(LOG_DOC_GENERATOR_RESPONSE_INVALID_MESSAGE_KEY, logMap);

            return false;
        }
        return true;
    }

    /**
     * Checks the document generator response contains the period end on within the description
     * values.
     *
     * @param response Contains the document generator response.
     * @return true when the description values contains the period_end_on key and its value is null
     * or blank.
     */
    private boolean isPeriodEndOnInDocGeneratorResponse(DocumentGeneratorResponse response) {

        if (response.getDescriptionValues() == null ||
            !response.getDescriptionValues().containsKey(PERIOD_END_ON) ||
            StringUtils.isBlank(response.getDescriptionValues().get(PERIOD_END_ON))) {

            Map<String, Object> logMap = new HashMap<>();
            logMap.put(LOG_MESSAGE_KEY,
                "Period end on has not been set within the description_values in the Document Generator Response");

            LOGGER.error(LOG_DOC_GENERATOR_RESPONSE_INVALID_MESSAGE_KEY, logMap);
            return false;
        }

        return true;
    }

    /**
     * Get the ixbrl location stored in the document generator response values.
     *
     * @param response Contains the document generator response.
     * @return the location or null if ixbrl location has not been set.
     */
    private String getIxbrlLocationFromDocGeneratorResponse(DocumentGeneratorResponse response) {
        return Optional.of(response)
            .map(DocumentGeneratorResponse::getLinks)
            .map(Links::getLocation)
            .orElse(null);
    }
}