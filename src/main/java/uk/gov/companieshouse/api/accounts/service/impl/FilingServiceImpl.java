package uk.gov.companieshouse.api.accounts.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.filing.Data;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.filing.Link;
import uk.gov.companieshouse.api.accounts.model.ixbrl.Account;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.util.ixbrl.accountsbuilder.AccountsBuilder;
import uk.gov.companieshouse.api.accounts.util.ixbrl.ixbrlgenerator.DocumentGeneratorConnection;
import uk.gov.companieshouse.api.accounts.util.ixbrl.ixbrlgenerator.IxbrlGenerator;
import uk.gov.companieshouse.document.data.DocumentDescriptionHelper;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Service
public class FilingServiceImpl implements FilingService {

    private static final String API_KEY_ENV_VAR = "CHS_API_KEY";
    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";
    private static final String DOCUMENT_BUCKET_NAME_ENV_VAR = "DOCUMENT_BUCKET_NAME";
    private static final String DOCUMENT_RENDER_SERVICE_END_POINT = "/document-render/store";
    private static final String DOCUMENT_RENDER_SERVICE_HOST_ENV_VAR = "DOCUMENT_RENDER_SERVICE_HOST";
    private static final String IXBRL_LOCATION = "s3://%s/%s/%s";
    private static final String LINK_RELATIONSHIP = "accounts";
    private static final String PERIOD_END_ON = "period_end_on";

    private final EnvironmentReader environmentReader;
    private final ObjectMapper objectMapper;
    private final IxbrlGenerator ixbrlGenerator;
    private final AccountsBuilder accountsBuilder;

    @Autowired
    public FilingServiceImpl(
        EnvironmentReader environmentReader,
        ObjectMapper objectMapper,
        IxbrlGenerator ixbrlGenerator,
        AccountsBuilder accountsBuilder) {

        this.objectMapper = objectMapper;
        this.environmentReader = environmentReader;
        this.ixbrlGenerator = ixbrlGenerator;
        this.accountsBuilder = accountsBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Filing generateAccountFiling(Transaction transaction, CompanyAccountEntity accountEntity)
        throws IOException {
        Filing filing = null;

        if (isSmallFullAccount(accountEntity)) {
            filing = generateAccountFiling(transaction,
                getAccountType(AccountsType.SMALL_FULL_ACCOUNTS.getAccountType()));
        }

        return filing;
    }

    /**
     * Gets the account type information.
     *
     * @param accountTypeName - accounts type name. e.g. small-full, abridged, etc.
     * @return accounts type information.
     */
    private AccountsType getAccountType(String accountTypeName) {
        return AccountsType.getAccountsType(accountTypeName);
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
        Filing filing = null;
        Object accountObj = getAccountInformation(accountsType.getAccountType());

        if (accountObj != null) {
            String ixbrlLocation = callIxbrlGenerator(accountsType,
                generateJson(accountsType.getAccountType(), accountObj));

            if (ixbrlLocation != null && isValidIXBL()) {
                filing = createAccountFiling(transaction, accountsType, ixbrlLocation);
            }
        }

        return filing;
    }

    /**
     * Generate the json for the passed-in account. This is used in the request body when calling
     * the service.
     *
     * @param accountType - Account type information (e.g. small-full, abridged, etc)
     * @param accountObj - The account's type object that's being processed (e.g. Account)
     * @return null or ixbrl location
     * @throws JsonProcessingException
     */
    private String generateJson(String accountType, Object accountObj)
        throws JsonProcessingException {
        if (isSmallFullType(accountType)) {
            return generateCompanyAccountJSON((Account) accountObj);
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

        //TODO get correct periodEndOn. periodEndOn = Current Period's end date", mongo DB. Waiting for the API changes.
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

        return DocumentDescriptionHelper
            .getDescription(accountsType.getFilingDescriptionKey(), parameters);
    }

    /**
     * Validates the ixbrl against TNEP. This validation is driven by the env. variable and it can
     * be disable.
     */
    private boolean isValidIXBL() {

        boolean isIxbrlValid = true;
        if ("true".equals(getMandatoryEnvVariable(DISABLE_IXBRL_VALIDATION_ENV_VAR))) {
            //TODO TNEP validation needs to be added. Copy logic from abridged. Next PR
        }

        return isIxbrlValid;
    }

    /**
     * Generates the ixbrl by calling the document render service.
     *
     * @param accountsType - Account type information: account type, ixbrl's template name, account
     * @param requestBody - the http request request (json format)
     * @return The location where the service has stored the generated ixbrl.
     */
    private String callIxbrlGenerator(AccountsType accountsType, String requestBody)
        throws IOException {
        if (requestBody != null) {
            DocumentGeneratorConnection connection = getDocumentGeneratorConnection(accountsType,
                requestBody);
            return ixbrlGenerator.generateIXBRL(connection);
        }

        return null;
    }

    /**
     * Create DocumentGeneratorConnectionImpl instance containing the document render's request
     * settings. e.g. Method, Service URL, Body, etc.
     *
     * @param accountsType - Account type information: account type, ixbrl's template name, account
     * @param requestBody - the http request request (json format).
     * @return {@link DocumentGeneratorConnection} with the document render service settings.
     */
    private DocumentGeneratorConnection getDocumentGeneratorConnection(
        AccountsType accountsType,
        String requestBody) {
        DocumentGeneratorConnection connection = new DocumentGeneratorConnection();

        connection.setRequestMethod("POST");
        connection.setServiceURL(getServiceURL());
        connection.setRequestBody(requestBody);
        connection.setAuthorizationProperty(getAPIAuthorization());
        connection.setAssetId(accountsType.getAssetId());
        connection.setContentType(MediaType.TEXT_HTML_VALUE);
        connection.setAcceptType(MediaType.TEXT_HTML_VALUE);
        connection.setLocation(getIXBRLLocation(accountsType));
        connection.setTemplateName(accountsType.getTemplateName());
        connection.setSetDoOutPut(true);

        return connection;
    }

    /**
     * get the location the document render service needs to stored the ixbrl document. e.g.
     * "s3://dev-pdf-bucket/chs-dev/accounts/small_full_accounts"
     *
     * @param accountsType - Account type information: account type, ixbrl's template name, account
     * @return
     */
    private String getIXBRLLocation(AccountsType accountsType) {
        if (isSmallFullType(accountsType.getAccountType())) {
            return
                String.format(
                    IXBRL_LOCATION,
                    getMandatoryEnvVariable(DOCUMENT_BUCKET_NAME_ENV_VAR),
                    accountsType.getAssetId(),
                    accountsType.getResourceKey());
        }

        return null;
    }

    /**
     * Get the API key needed to call the service.
     */
    private String getAPIAuthorization() {
        return getMandatoryEnvVariable(API_KEY_ENV_VAR);
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

    /**
     * Generates the json for small full account.
     *
     * @param account - small full's account information.
     * @return The account marshaled to JSON and converted to String.
     * @throws JsonProcessingException
     */
    private String generateCompanyAccountJSON(Account account)
        throws JsonProcessingException {
        JSONObject accountsRequestBody = new JSONObject();
        accountsRequestBody.put("small_full_account", convertObjectToJson(account));

        return accountsRequestBody.toString();
    }

    /**
     * Marshall object to json.
     *
     * @param obj - object is being processed. e.g. Account object
     * @return {@link JSONObject}
     * @throws JsonProcessingException
     */
    private <T> JSONObject convertObjectToJson(T obj) throws JsonProcessingException {
        return new JSONObject(objectMapper.writeValueAsString(obj));
    }

    /**
     * Builds the account object based on the passed in account type.
     *
     * @param accountType the account type, e.g. small-full. Used to build correct object.
     */
    private Object getAccountInformation(String accountType) throws IOException {
        if (isSmallFullType(accountType)) {
            return getSmallFullAccount();
        }

        return null;
    }

    /**
     * Checks if accountEntity is a small full by checking the links within the data.
     *
     * @param accountEntity - Accounts information.
     * @return true if small full account.
     */
    private boolean isSmallFullAccount(CompanyAccountEntity accountEntity) {
        return accountEntity.getData().getLinks().get(LinkType.SMALL_FULL.getLink()) != null;
    }

    /**
     * Checks if filing type is small full.
     *
     * @param filingType - name of the filing type.
     * @return true if small full account.
     */
    private boolean isSmallFullType(String filingType) {
        return AccountsType.SMALL_FULL_ACCOUNTS.getAccountType().equals(filingType);
    }

    /**
     * Get the small full account information.
     */
    private Account getSmallFullAccount() throws IOException {
        return (Account) accountsBuilder.buildAccount();
    }
}
