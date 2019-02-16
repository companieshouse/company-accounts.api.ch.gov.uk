package uk.gov.companieshouse.api.accounts.service.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.accountsdates.AccountsDatesHelper;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.filing.Data;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.filing.Link;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.DocumentGeneratorResponse;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.service.TnepValidationService;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.filetransfer.FileTransferTool;
import uk.gov.companieshouse.api.accounts.utility.ixbrl.DocumentGeneratorCaller;
import uk.gov.companieshouse.api.accounts.validation.ixbrl.DocumentGeneratorResponseValidator;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class FilingServiceImpl implements FilingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String LOG_MESSAGE_KEY                  = "message";
    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";
    private static final String LINK_RELATIONSHIP                = "accounts";
    private static final String PERIOD_END_ON                    = "period_end_on";

    private final DocumentGeneratorCaller            documentGeneratorCaller;
    private final EnvironmentReader                  environmentReader;
    private final DocumentGeneratorResponseValidator documentGeneratorResponseValidator;
    private final AccountsDatesHelper                accountsDatesHelper;
    private final FileTransferTool                   fileTransferTool;
    private final TnepValidationService              tnepValidationService;

    @Autowired
    public FilingServiceImpl(DocumentGeneratorCaller documentGeneratorCaller, EnvironmentReader environmentReader,
            DocumentGeneratorResponseValidator documentGeneratorResponseValidator,
            AccountsDatesHelper accountsDatesHelper, FileTransferTool fileTransferTool,
            TnepValidationService tnepValidationService) {

        this.documentGeneratorCaller = documentGeneratorCaller;
        this.environmentReader = environmentReader;
        this.documentGeneratorResponseValidator = documentGeneratorResponseValidator;
        this.accountsDatesHelper = accountsDatesHelper;
        this.fileTransferTool = fileTransferTool;
        this.tnepValidationService = tnepValidationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Filing generateAccountFiling(Transaction transaction, CompanyAccount companyAccount) {

        AccountsType accountType = getAccountType(companyAccount);
        if (accountType != null) {
            return generateAccountFiling(transaction, companyAccount, accountType);
        }

        return null;
    }

    /**
     * Get account type by checking the account type link within the account's
     * data.
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
     * @return {@link Filing} - null or filing with the filing information (e.g.
     *         ixbrl location, accounts name, etc)
     */
    private Filing generateAccountFiling(Transaction transaction, CompanyAccount companyAccount,
            AccountsType accountsType) {

        DocumentGeneratorResponse documentGeneratorResponse = getDocumentGeneratorResponse(companyAccount);

        if (documentGeneratorResponse != null && isDocumentGeneratorResponseValid(documentGeneratorResponse)
                && isValidIxbrl(documentGeneratorResponse.getLinks().getLocation())) {

            return createAccountFiling(transaction, accountsType, documentGeneratorResponse);
        }

        return null;
    }

    /**
     * Calls the document generator to obtain the information needed to build
     * the filing object: e.g. ixbrl location, description and period end date.
     *
     * @return The location where the service has stored the generated ixbrl.
     */
    private DocumentGeneratorResponse getDocumentGeneratorResponse(CompanyAccount companyAccount) {

        Map<String, Object> logMap = new HashMap<>();

        String companyAccountsURI = companyAccount.getLinks().get(CompanyAccountLinkType.SELF.getLink());

        try {
            DocumentGeneratorResponse documentGeneratorResponse = documentGeneratorCaller
                    .callDocumentGeneratorService(companyAccountsURI);

            if (documentGeneratorResponse != null) {
                return documentGeneratorResponse;
            }
            logMap.put("company_account_self_link", companyAccountsURI);
            logMap.put(LOG_MESSAGE_KEY, "Document generator response call has returned null");
            LOGGER.error("FilingServiceImpl: Document Generator call failed", logMap);

            return null;

        } catch (IllegalArgumentException e) {
            logMap.put(LOG_MESSAGE_KEY, "Document Generator has thrown an Illegal exception");
            LOGGER.error("FilingServiceImpl: Document Generator call failed", e, logMap);

            return null;
        }
    }

    /**
     * Downloads the ixbrl content and call the tnep validation service if the
     * download was successful. The tnep validation service needs the location
     * and the data to performs the validation. This validation is driven by the
     * environment and it can be disable.
     *
     * @param fileLocation
     *            - location of the file that needs to be validated.
     * @return true is valid ixbrl.
     */
    private boolean isValidIxbrl(String fileLocation) {

        boolean isIxbrlValid = false;
        if (!environmentReader.getOptionalBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR)) {
            String ixbrlData = downloadIxbrlFromLocation(fileLocation);

            if (ixbrlData != null) {
                isIxbrlValid = tnepValidationService.validate(ixbrlData, fileLocation);
            }
        }

        return isIxbrlValid;
    }

    /**
     * Generates the filing based on the Filing model.
     *
     * @param transaction
     *            - transaction information
     * @param accountsType
     *            - Account type information: account type, ixbrl's template
     *            name, account
     * @param documentGeneratorResponse
     *            - the location where the ixbrl is stored.
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
     * Retrieve the period end date from the document generator response, and
     * set the date in the expected format YYYY-mm-dd.
     *
     * @param response
     *            information from the document generator call.
     * @return period end dated formatted
     */
    private LocalDate getPeriodEndDateFormatted(DocumentGeneratorResponse response) {

        return accountsDatesHelper.convertStringToDate(response.getDescriptionValues().get(PERIOD_END_ON));
    }

    /**
     * Get the filing data, it contains period end date and the links (ixbrl
     * location and relationship link).
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
     * Get the Link containing the ixbrl location and the relationship link e.g.
     * accounts.
     *
     * @param ixbrlLocation
     *            - the location where ixbrl is stored
     * @return {@link List < Link >}
     */
    private List<Link> createFilingLinks(String ixbrlLocation) {
        Link link = new Link();
        link.setRelationship(LINK_RELATIONSHIP);
        link.setHref(ixbrlLocation);

        return Arrays.asList(link);
    }

    /**
     * Call document generator response to validate response. It needs to
     * contain: Description, period end on (within description values) and the
     * ixbrl location are needed.
     *
     * @param response
     *            document generator response.
     * @return true if the response contains all the needed information.
     */
    private boolean isDocumentGeneratorResponseValid(DocumentGeneratorResponse response) {
        return documentGeneratorResponseValidator.isDocumentGeneratorResponseValid(response);
    }

    /**
     * Calls the fileTransferTool to download file from public location.
     *
     * @param location
     *            - the ixbrl location, which is a public location.
     * @return the actual ixbrl content. Or null if download fails.
     */
    private String downloadIxbrlFromLocation(String location) {

        String ixbrlData = fileTransferTool.downloadFileFromLocation(location);

        if (StringUtils.isEmpty(ixbrlData)) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put(LOG_MESSAGE_KEY, "The ixbrl data content is empty");

            LOGGER.error("FilingServiceImpl: File Transfer Tool has fail to download file", logMap);

            return null;
        }

        return ixbrlData;
    }
}