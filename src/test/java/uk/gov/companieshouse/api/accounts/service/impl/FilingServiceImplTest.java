package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.accountsdates.AccountsDatesHelper;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.filing.Data;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.filing.Link;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.DocumentGeneratorResponse;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.Links;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.service.TnepValidationService;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.filetransfer.FileTransferTool;
import uk.gov.companieshouse.api.accounts.utility.ixbrl.DocumentGeneratorCaller;
import uk.gov.companieshouse.api.accounts.validation.ixbrl.DocumentGeneratorResponseValidator;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class FilingServiceImplTest {

    private static final String COMPANY_NUMBER                   = "9999999";
    private static final String TRANSACTION_ID                   = "1234561-1234561-1234561";
    private static final String ACCOUNTS_ID                      = "1234561";
    private static final String SMALL_FULL_ID                    = "smallFullId";
    private static final String IXBRL_LOCATION                   = "http://test/ixbrl_bucket_location";
    private static final String IXBRL_DATA                       = getIxbrlContent();
    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";
    private static final String ACCOUNTS_SELF_REF                = "/transactions/" + TRANSACTION_ID
            + "/company-accounts/" + ACCOUNTS_ID;

    private static final String PERIOD_END_ON_KEY           = "period_end_on";
    private static final String PERIOD_END_ON_VALUE         = "2018-01-18";
    private static final LocalDate PERIOD_END_ON_VALUE_DATE = LocalDate.of(2018, 1, 18);
    private static final String DOC_GEN_ACCOUNT_DESCRIPTION = "Small full accounts made up to {period_end_on}";
    private static final String FINAL_ACCOUNT_DESCRIPTION   = "Small full accounts made up to 18 January 2018";
    private static final String ACCOUNTS_LINKS_RELATIONSHIP = "accounts";

    private FilingService             filingService;
    private CompanyAccount            companyAccount;
    private Transaction               transaction;
    private DocumentGeneratorResponse documentGeneratorResponse;

    @Mock
    private DocumentGeneratorCaller            documentGeneratorCallerMock;
    @Mock
    private EnvironmentReader                  environmentReaderMock;
    @Mock
    private DocumentGeneratorResponseValidator docGeneratorResponseValidatorMock;
    @Mock
    private AccountsDatesHelper                accountsDatesHelperMock;
    @Mock
    private FileTransferTool                   fileTransferToolMock;
    @Mock
    private TnepValidationService              tnepValidationServiceMock;

    private static String getIxbrlContent() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<html xmlns:ixt2=\"http://www.xbrl.org/inlineXBRL/transformation/2011-07-31\">\n" + "  <head>\n"
                + "    <meta content=\"application/xhtml+xml; charset=UTF-8\" http-equiv=\"content-type\" />\n"
                + "    <title>\n" + "            TEST COMPANY\n" + "        </title>\n" + "  <body xml:lang=\"en\">\n"
                + "    <div class=\"accounts-body \">\n" + "      <div id=\"your-account-type\" class=\"wholedoc\">\n"
                + "      </div>\n" + "    </div>\n" + "   </body>\n" + "</html>\n";
    }

    @BeforeEach
    void setUpBeforeEach() {
        transaction = createTransaction();
        companyAccount = createAccount();

        filingService = new FilingServiceImpl(documentGeneratorCallerMock, environmentReaderMock,
                docGeneratorResponseValidatorMock, accountsDatesHelperMock, fileTransferToolMock,
                tnepValidationServiceMock);
    }

    @Test
    @DisplayName("Tests the filing generation. Happy path")
    void shouldGenerateFiling() {
        documentGeneratorResponse = createDocumentGeneratorResponse();

        doReturn(documentGeneratorResponse).when(documentGeneratorCallerMock)
                .callDocumentGeneratorService(ACCOUNTS_SELF_REF);

        doReturn(true).when(docGeneratorResponseValidatorMock)
                .isDocumentGeneratorResponseValid(documentGeneratorResponse);

        doReturn(false).when(environmentReaderMock).getOptionalBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR);

        when(fileTransferToolMock.downloadFileFromLocation(IXBRL_LOCATION))
            .thenReturn(IXBRL_DATA);

        when(tnepValidationServiceMock.validate(IXBRL_DATA, IXBRL_LOCATION)).thenReturn(true);

        doReturn(PERIOD_END_ON_VALUE_DATE).when(accountsDatesHelperMock).convertStringToDate(PERIOD_END_ON_VALUE);
        doReturn("18 January 2018").when(accountsDatesHelperMock).convertLocalDateToDisplayDate(PERIOD_END_ON_VALUE_DATE);
        
        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);

        verifyAllMockCalls();
        verifyFilingData(filing);
    }

    @Test
    @DisplayName("Tests the filing not generated when small full accounts link is not present")
    void shouldNotGenerateFilingAsSmallFullLinkNotPresentWithinAccountData() {
        companyAccount.setLinks(new HashMap<>());
        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);

        assertNull(filing);
    }

    @Test
    @DisplayName("Tests the filing not generated when document generator response fails")
    void shouldNotGenerateFilingAsDocumentGeneratorResponseIsNull() {

        doReturn(null).when(documentGeneratorCallerMock).callDocumentGeneratorService(ACCOUNTS_SELF_REF);

        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);

        verifyDocumentGeneratorCallerMock();
        assertNull(filing);
    }

    @Test
    @DisplayName("Tests the filing not generated when document generator response validation fails")
    void shouldNotGenerateFilingAsPeriodEndOnKeyIsNotInDocGeneratorResponse() {

        documentGeneratorResponse = createDocumentGeneratorResponse();

        doReturn(documentGeneratorResponse).when(documentGeneratorCallerMock)
                .callDocumentGeneratorService(ACCOUNTS_SELF_REF);

        doReturn(false).when(docGeneratorResponseValidatorMock)
                .isDocumentGeneratorResponseValid(documentGeneratorResponse);

        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);

        verifyDocumentGeneratorCallerMock();
        verifyDocumentGeneratorResponseValidatorMock();
        assertNull(filing);
    }

    @Test
    @DisplayName("Tests the filing not generated when document generator call throws an exception")
    void shouldNotGenerateFilingAsDocumentGeneratorCallThrowsAnException() {

        documentGeneratorResponse = createDocumentGeneratorResponse();

        doThrow(IllegalArgumentException.class).when(documentGeneratorCallerMock)
                .callDocumentGeneratorService(ACCOUNTS_SELF_REF);

        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);

        verifyDocumentGeneratorCallerMock();
        assertNull(filing);
    }

    @Test
    @DisplayName("Tests the filing not generated when file transfer tool call fails to download ixbrl")
    void shouldNotGenerateFilingAsFileTransferToolCallFailsToDownloadIxbrl() {
        documentGeneratorResponse = createDocumentGeneratorResponse();

        doReturn(documentGeneratorResponse).when(documentGeneratorCallerMock)
                .callDocumentGeneratorService(ACCOUNTS_SELF_REF);

        doReturn(true).when(docGeneratorResponseValidatorMock)
                .isDocumentGeneratorResponseValid(documentGeneratorResponse);

        doReturn(false).when(environmentReaderMock).getOptionalBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR);

        when(fileTransferToolMock.downloadFileFromLocation(IXBRL_LOCATION))
            .thenReturn(null);

        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);

        verifyDocumentGeneratorCallerMock();
        verifyEnviromentReaderMockForDisableIxbrlVariable();
        verifyDocumentGeneratorResponseValidatorMock();
        verifyfileTransferToolMock();

        assertNull(filing);
    }

    @Test
    @DisplayName("Tests the filing not generated when tnep validation has failed. Ixbrl is invalid")
    void shouldNotGenerateFilingAsTnepValidationHasFailed() {
        documentGeneratorResponse = createDocumentGeneratorResponse();

        doReturn(documentGeneratorResponse).when(documentGeneratorCallerMock)
                .callDocumentGeneratorService(ACCOUNTS_SELF_REF);

        doReturn(true).when(docGeneratorResponseValidatorMock)
                .isDocumentGeneratorResponseValid(documentGeneratorResponse);

        when(environmentReaderMock.getOptionalBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR)).thenReturn(false);

        when(fileTransferToolMock.downloadFileFromLocation(IXBRL_LOCATION))
            .thenReturn(IXBRL_DATA);

        when(tnepValidationServiceMock.validate(IXBRL_DATA, IXBRL_LOCATION)).thenReturn(false);

        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);

        verifyDocumentGeneratorCallerMock();
        verifyEnviromentReaderMockForDisableIxbrlVariable();
        verifyDocumentGeneratorResponseValidatorMock();
        verifyfileTransferToolMock();
        verifyTnepValidationServiceMock();

        assertNull(filing);
    }

    /**
     * Verify small full data the data within the filing object is correct
     *
     * @param filing
     */
    private void verifyFilingData(Filing filing) {
        Assert.assertNotNull(filing);
        assertEquals(COMPANY_NUMBER, filing.getCompanyNumber());
        assertEquals(AccountsType.SMALL_FULL_ACCOUNTS.getAccountType(), filing.getDescriptionIdentifier());
        assertEquals(FINAL_ACCOUNT_DESCRIPTION, filing.getDescription());
        assertEquals(AccountsType.SMALL_FULL_ACCOUNTS.getKind(), filing.getKind());
        Assert.assertNotNull(filing.getDescriptionValues());
        Assert.assertNotNull(filing.getDescriptionValues().get(PERIOD_END_ON_KEY));
        assertEquals(PERIOD_END_ON_VALUE, filing.getDescriptionValues().get(PERIOD_END_ON_KEY));

        Data data = filing.getData();
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getPeriodEndOn());

        List<Link> links = data.getLinks();
        Assert.assertNotNull(links);
        assertEquals(1, links.size());

        Link link = links.get(0);
        Assert.assertNotNull(link);
        assertEquals(ACCOUNTS_LINKS_RELATIONSHIP, link.getRelationship());
        assertEquals(IXBRL_LOCATION, link.getHref());
    }

    private void verifyAllMockCalls() {
        verifyDocumentGeneratorCallerMock();
        verifyEnviromentReaderMockForDisableIxbrlVariable();
        verifyDocumentGeneratorResponseValidatorMock();
        verifyfileTransferToolMock();
        verifyTnepValidationServiceMock();
        verifyAccountsDatesHelperMock();
    }

    private void verifyDocumentGeneratorCallerMock() {
        verify(documentGeneratorCallerMock, times(1)).callDocumentGeneratorService(ACCOUNTS_SELF_REF);
    }

    private void verifyDocumentGeneratorResponseValidatorMock() {
        verify(docGeneratorResponseValidatorMock, times(1)).isDocumentGeneratorResponseValid(documentGeneratorResponse);
    }

    private void verifyEnviromentReaderMockForDisableIxbrlVariable() {
        verify(environmentReaderMock, times(1)).getOptionalBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR);
    }

    private void verifyfileTransferToolMock() {
        verify(fileTransferToolMock, times(1))
            .downloadFileFromLocation(IXBRL_LOCATION);
    }

    private void verifyTnepValidationServiceMock() {
        verify(tnepValidationServiceMock, times(1)).validate(IXBRL_DATA, IXBRL_LOCATION);
    }

    private void verifyAccountsDatesHelperMock() {
        verify(accountsDatesHelperMock, times(2)).convertStringToDate(PERIOD_END_ON_VALUE);
        verify(accountsDatesHelperMock, times(1)).convertLocalDateToDisplayDate(PERIOD_END_ON_VALUE_DATE);
    }

    /**
     * Builds Account Entity object.
     */
    private CompanyAccount createAccount() {
        CompanyAccount account = new CompanyAccount();

        account.setEtag("etagForTesting");
        account.setKind(ACCOUNTS_LINKS_RELATIONSHIP);
        account.setLinks(createAccountEntityLinks());
        account.setPeriodEndOn(PERIOD_END_ON_VALUE_DATE);

        return account;
    }

    /**
     * Creates the links for the accounts data.
     */
    private Map<String, String> createAccountEntityLinks() {
        Map<String, String> dataLinks = new HashMap<>();

        dataLinks.put(BasicLinkType.SELF.getLink(),
                String.format("/transactions/%s/company-accounts/%s", TRANSACTION_ID, ACCOUNTS_ID));

        dataLinks.put(CompanyAccountLinkType.SMALL_FULL.getLink(), String.format(
                "/transactions/%s/company-accounts/%s/small-full/%s", TRANSACTION_ID, ACCOUNTS_ID, SMALL_FULL_ID));

        return dataLinks;
    }

    /**
     * Create an transaction object.
     */
    private Transaction createTransaction() {
        Transaction transactionObject = new Transaction();
        transactionObject.setId(TRANSACTION_ID);
        transactionObject.setCompanyNumber(COMPANY_NUMBER);
        return transactionObject;
    }

    /**
     * Create a Document Generator Response with all needed information to
     * generate the filing.
     */
    private DocumentGeneratorResponse createDocumentGeneratorResponse() {

        DocumentGeneratorResponse response = new DocumentGeneratorResponse();

        response.setDescriptionIdentifier("small-full-accounts");
        response.setSize("999999");
        response.setLinks(createIxbrlLink(IXBRL_LOCATION));
        response.setDescription(DOC_GEN_ACCOUNT_DESCRIPTION);
        response.setDescriptionValues(createDescriptionValues(PERIOD_END_ON_KEY, PERIOD_END_ON_VALUE));

        return response;
    }

    /**
     * Create links object containing the ixbrl location.
     *
     * @param ixbrlLocation
     *            Ixbrl location to be set in the links
     * @return
     */
    private Links createIxbrlLink(String ixbrlLocation) {

        Links links = new Links();
        links.setLocation(ixbrlLocation);
        return links;
    }

    /**
     * Create the description values map with the key and value passed in
     *
     * @param keyId
     *            key name to use it in the map
     * @param keyValue
     *            key value to use in the map
     * @return
     */
    private Map<String, String> createDescriptionValues(String keyId, String keyValue) {

        Map<String, String> descriptionValues = new HashMap<>();
        descriptionValues.put(keyId, keyValue);
        return descriptionValues;
    }

}