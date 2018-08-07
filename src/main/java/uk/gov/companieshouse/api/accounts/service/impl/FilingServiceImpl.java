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
import uk.gov.companieshouse.api.accounts.model.filing.Data;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.filing.Link;
import uk.gov.companieshouse.api.accounts.model.ixbrl.Account;
import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.CalledUpSharedCapitalNotPaid;
import uk.gov.companieshouse.api.accounts.model.ixbrl.company.Company;
import uk.gov.companieshouse.api.accounts.model.ixbrl.notes.Notes;
import uk.gov.companieshouse.api.accounts.model.ixbrl.notes.PostBalanceSheetEvents;
import uk.gov.companieshouse.api.accounts.model.ixbrl.period.Period;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionStatus;
import uk.gov.companieshouse.api.accounts.util.ixbrl.IxbrlGenerator;
import uk.gov.companieshouse.api.accounts.util.ixbrl.DocumentGeneratorConnection;
import uk.gov.companieshouse.document.data.DocumentDescriptionHelper;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

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
    private static final String SMALL_FULL_ACCOUNT = "small-full";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IxbrlGenerator ixbrlGenerator;

    private EnvironmentReader environmentReader;

    /**
     * Generate a filing with the ixbrl location that is generated for transaction and accounts id.
     *
     * @return {@link Filing}
     */
    @Override
    public Filing generateAccountFiling(String transactionId, String accountsId)
        throws IOException {
        Filing filing = null;
        Transaction transaction = getTransaction(transactionId);

        if (SMALL_FULL_ACCOUNT.equals(transaction.getKind())) {
            filing = generateAccountFiling(transaction, getAccountType(SMALL_FULL_ACCOUNT));
        }

        return filing;
    }

    private AccountsType getAccountType(String accountTypeName) {
        return AccountsType.getAccountsType(accountTypeName);
    }

    /**
     * Get the transaction for the transaction id by calling the SDK.
     */
    private Transaction getTransaction(String transactionId) {
        //TODO below code to be replaced by SDK call. Functionality not there yet.
        Transaction transaction = new Transaction();
        transaction.setCompanyNumber("12345678");
        transaction.setStatus(TransactionStatus.CLOSED.getStatus());
        transaction.setId(transactionId);
        transaction.setKind(SMALL_FULL_ACCOUNT);

        return transaction;
    }

    /**
     * Generate the filing for the account type passed in.
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
     * Generate the json for the passed-in account. This is used in the request body when
     * calling the service.
     * *
     * @param accountType
     * @return
     */
    private String generateJson(String accountType, Object accountObj)
        throws JsonProcessingException {
        if (SMALL_FULL_ACCOUNT.equals(accountType)) {
            return generateCompanyAccountJSON((Account) accountObj);
        }
        return null;
    }

    /**
     * Generates the filing based on the Filing model.
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
     */
    private Map<String, String> getDescriptionValues(LocalDate periodEndDate) {
        Map<String, String> descriptionValues = new HashMap<>();
        descriptionValues.put(PERIOD_END_ON, periodEndDate.toString());

        return descriptionValues;
    }

    /**
     * Get the description for the filing. The description for the account type is retrieved by
     * using DocumentDescriptionHelper class.
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
     * @param accountsType - account type. e.g. small-full, abridged (when migrated)
     * @param requestBody - the body of the http request.
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
     * Returns the location where the ixbrl is stored once it has been generated by the service.
     * E.g. "s3://dev-pdf-bucket/chs-dev/accounts/small_full_accounts"
     * "s3://dev-pdf-bucket/chs-dev/accounts/abridged_accounts"
     *
     * @return the location for the account type passed in.
     */
    private String getIXBRLLocation(AccountsType accountsType) {
        if (SMALL_FULL_ACCOUNT.equals(accountsType.getAccountType())) {
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
        if (environmentReader == null) {
            environmentReader = new EnvironmentReaderImpl();
        }

        return environmentReader.getMandatoryString(envVariable);
    }


    /**
     * Generates the json for small full account.
     *
     * @return The account marshaled to JSON and converted to String.
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
     * @return {@link JSONObject}
     */
    private <T> JSONObject convertObjectToJson(T obj) throws JsonProcessingException {
        return new JSONObject(objectMapper.writeValueAsString(obj));
    }

    /**
     * Builds the account object based on the passed in account type.
     *
     * @param accountType the account type, e.g. small-full. Used to build correct object.
     */
    private Object getAccountInformation(String accountType) {
        if (SMALL_FULL_ACCOUNT.equals(accountType)) {
            return getSmallFullAccount();
        }

        return null;
    }

    /**
     * Builds the small full accounts model. Functionality needs to be change.
     */
    private Account getSmallFullAccount() {
        //TODO: REMOVE HARDCODED VALUES. Use functionality to retrieve the accounts information when built in the API.
        Account account = new Account();
        account.setPeriod(getAccountPeriod());
        account.setBalanceSheet(getBalanceSheet());
        account.setNotes(getNotes());
        account.setCompany(getCompany());

        return account;
    }

    private Company getCompany() {
        Company company = new Company();
        company.setCompanyName("SC344891");
        company.setCompanyNumber("MYRETON RENEWABLE ENERGY LIMITED");

        return company;
    }

    private Notes getNotes() {
        Notes notes = new Notes();
        PostBalanceSheetEvents postBalanceSheetEvents = new PostBalanceSheetEvents();
        postBalanceSheetEvents.setCurrentPeriodDateFormatted("16 December 2017");
        postBalanceSheetEvents.setPostBalanceSheetEventsInfo("test post balance note");
        notes.setPostBalanceSheetEvents(postBalanceSheetEvents);

        return notes;
    }

    private Period getAccountPeriod() {
        Period period = new Period();
        period.setCurrentPeriodStartOn("2017-05-01");
        period.setCurrentPeriodEndsOn("2018-05-01");
        period.setPreviousPeriodStartOn("2016-12-01");
        period.setPreviousPeriodEndsOn("2016-01-01");

        return period;
    }

    /**
     * Build BalanceSheet model by using information from database
     */
    private BalanceSheet getBalanceSheet() {
        //TODO: remove hardcoded values for actual db call
        BalanceSheet balanceSheet = new BalanceSheet();

        CalledUpSharedCapitalNotPaid calledUpSharedCapitalNotPaid = new CalledUpSharedCapitalNotPaid();
        calledUpSharedCapitalNotPaid.setCurrentAmount(9);
        calledUpSharedCapitalNotPaid.setPreviousAmount(99);

        balanceSheet.setCalledUpSharedCapitalNotPaid(calledUpSharedCapitalNotPaid);
        balanceSheet.setCurrentPeriodDateFormatted("16 December 2017");
        balanceSheet.setPreviousPeriodDateFormatted("16 December 2017");

        return balanceSheet;
    }
}
