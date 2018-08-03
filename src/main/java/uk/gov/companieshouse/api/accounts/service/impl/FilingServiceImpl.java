package uk.gov.companieshouse.api.accounts.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
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
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

@Service
public class FilingServiceImpl implements FilingService {

    private static final String API_KEY = "CHS_API_KEY";
    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";
    private static final String DOCUMENT_BUCKET_NAME_ENV_VAR = "DOCUMENT_BUCKET_NAME";
    private static final String DOCUMENT_RENDER_SERVICE_END_POINT = "/document-render/store";
    private static final String DOCUMENT_RENDER_SERVICE_HOST_ENV_VAR = "DOCUMENT_RENDER_SERVICE_HOST";
    private static final String IXBRL_LOCATION = "s3://%s/%s/%s";
    private static final String SMALL_FULL_ACCOUNT = "small-full";
    private static final String TYPE_TEXT_HTML = "text/html";

    @Autowired
    private ObjectMapper objectMapper;
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
            filing = generateAccountFiling(getAccountType(SMALL_FULL_ACCOUNT));
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
        transaction.setCompanyNumber("SC344891");
        transaction.setStatus(TransactionStatus.CLOSED.getStatus());
        transaction.setId(transactionId);
        transaction.setKind(SMALL_FULL_ACCOUNT);

        return transaction;
    }

    /**
     * Generate the filing for the account type passed in.
     */
    private Filing generateAccountFiling(AccountsType accountsType)
        throws IOException {
        Filing filing = null;
        Object accountObj = getAccountInformation(accountsType.getAccountType());

        if (accountObj != null) {
            String ixbrlLocation =
                callServiceToGenerateIXBRL(accountsType,
                    generateJson(accountsType.getAccountType(), accountObj));

            //callServiceToGenerateIXBRL2(accountsType, accountObj);

            if (ixbrlLocation != null && isValidIXBL()) {
                //TODO create the filing. Copy across from abridged since filing model has not changed.
                filing = createAccountFiling();
            }
        }

        return filing;
    }

    /**
     *
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
    private Filing createAccountFiling() {
        //TODO generates the filing
        return new Filing();
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
    private String callServiceToGenerateIXBRL(AccountsType accountsType, String requestBody)
        throws IOException {
        if (requestBody != null) {
            HttpURLConnection connection = createConnectionForAccount(accountsType);

            try {
                try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                    out.write(requestBody.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    return connection.getHeaderField("Location");
                }
            } finally {
                connection.disconnect();
            }
        }

        return null;
    }


    private String callServiceToGenerateIXBRL2(AccountsType accountsType, Object requestBody)
        throws IOException {
        String location = getIXBRLLocation(accountsType);
        if (location != null && requestBody != null) {

            HttpHeaders requestHeaders = new HttpHeaders();

            requestHeaders.set("Authorization", getAPIAuthorization());
            requestHeaders.set("assetId", accountsType.getAssetId());
            requestHeaders.set("Location", location);
            requestHeaders.set("templateName", accountsType.getTemplateName());
            requestHeaders.set("Accept", TYPE_TEXT_HTML);
            requestHeaders.add("Content-Type", TYPE_TEXT_HTML);

            HttpEntity<Account> entity = new HttpEntity<>((Account) requestBody, requestHeaders);

            RestTemplate restTemplate = new RestTemplate();
            String serviceUrl = getServiceURL();
            ResponseEntity<String> response = restTemplate
                .exchange(serviceUrl, HttpMethod.POST, entity, String.class);

            //String response = restTemplate.postForObject(serviceUrl, entity, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return response.getHeaders().get("Location").toString();
            }
        }

        return null;
    }

    /**
     * Get the connection information needed to call the service that generates the IXBRL.
     *
     * @param accountsType Account type. e.g. small full, abridged, etc.
     * @return {@link HttpURLConnection}
     */
    private HttpURLConnection createConnectionForAccount(AccountsType accountsType)
        throws IOException {

        //TODO this will change. Get information from environment variables.
        String serviceUrl = getServiceURL();
        HttpURLConnection connection = (HttpURLConnection) new URL(serviceUrl).openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", getAPIAuthorization());
        connection.setRequestProperty("assetId", accountsType.getAssetId());
        connection.setRequestProperty("Content-Type", TYPE_TEXT_HTML);
        connection.setRequestProperty("Accept", TYPE_TEXT_HTML);
        connection.setRequestProperty("Location", getIXBRLLocation(accountsType));
        connection.setRequestProperty("templateName", accountsType.getTemplateName());
        connection.setDoOutput(true);

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
        return getMandatoryEnvVariable(API_KEY);
    }

    /**
     * Build the service URL that will be used to create the IXBRL. E.g.
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
     * Generate the json for the passed-in account. This will be used in the request body when
     * calling the service.
     *
     * @return The account marshaled to JSON and converted to String.
     */
    private String generateCompanyAccountJSON(Account account)
        throws JsonProcessingException {
        JSONObject accountsRequestBody = new JSONObject();
        accountsRequestBody.put("small_full_accounts", convertObjectToJson(account));

        return accountsRequestBody.toString();
    }

    /**
     * Marshall object to json.
     *
     * @return {@link JSONObject}
     */
    private <T> JSONObject convertObjectToJson(T obj) throws JsonProcessingException {
        String jsonString = "";
        jsonString = objectMapper.writeValueAsString(obj);

        return new JSONObject(jsonString);
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
