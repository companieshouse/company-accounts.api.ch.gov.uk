package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
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
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.util.DocumentDescriptionHelper;
import uk.gov.companieshouse.api.accounts.util.ixbrl.DocumentGeneratorCaller;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class FilingServiceImplTest {

    private static final String TRANSACTION_ID = "1234561-1234561-1234561";
    private static final String ACCOUNTS_ID = "1234561";
    private static final String SMALL_FULL_ID = "smallFullId";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String COMPANY_NAME = "TEST COMPANY LIMITED";
    private static final String IXBRL_LOCATION = "http://test/ixbrl_bucket_location";
    private static final String LINK_RELATIONSHIP = "accounts";
    private static final String PERIOD_END_ON_KEY = "period_end_on";
    private static final String DOCUMENT_RENDER_SERVICE_HOST_ENV_VAR = "DOCUMENT_RENDER_SERVICE_HOST";
    private static final String API_KEY_ENV_VAR = "CHS_API_KEY";
    private static final String DOCUMENT_BUCKET_NAME_ENV_VAR = "DOCUMENT_BUCKET_NAME";
    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";
    private static final String TRANSACTION_DATE_FORMAT = "EEEE, MMM dd, yyyy HH:mm:ss a";
    private static final String PREVIOUS_PERIOD_FORMATTED = "01 January 2017";
    private static final String SMALL_FULL_ACCOUNT_JSON_FILE_PATH = "filing/json/small-full-account.json";
    private static String PREVIOUS_START_ON = "2016-01-01";
    private static String PREVIOUS_PERIOD_END_ON = "2016-12-01";
    private final static String FILINGS_DESCRIPTION =
        "Small full accounts made up to " + PREVIOUS_PERIOD_END_ON;
    private static String CURRENT_PERIOD_START_ON = "2017-05-01";
    private static String CURRENT_PERIOD_END_ON = "2018-05-01";
    private static String CURRENT_PERIOD_FORMATTED = "01 December 2017";

    @Captor
    private ArgumentCaptor<String> argCaptor;

    private Transaction transaction;
    private CompanyAccountEntity companyAccountEntity;
    private FilingServiceImpl filingService;
    private String smallFullAcountJson;

    @Mock
    private EnvironmentReader environmentReaderMock;
    @Mock
    private ObjectMapper objectMapperMock;
    @Mock
    private DocumentGeneratorCaller documentGeneratorCallerMock;
    @Mock
    private DocumentDescriptionHelper documentDescriptionHelperMock;

    @BeforeAll
    void setUpBeforeAll() throws IOException, URISyntaxException {
        smallFullAcountJson = getFileContentFromResource(SMALL_FULL_ACCOUNT_JSON_FILE_PATH);
    }

    @BeforeEach
    void setUpBeforeEach() throws ParseException {
        transaction = createTransaction();
        companyAccountEntity = createAccountEntity();

        filingService = new FilingServiceImpl(
            environmentReaderMock,
            documentDescriptionHelperMock,
            documentGeneratorCallerMock);
    }

    @DisplayName("Tests the filing generation. Happy path")
    @Test
    void shouldGenerateFiling() throws IOException, NoSuchAlgorithmException {

        doReturn(IXBRL_LOCATION).when(documentGeneratorCallerMock).generateIxbrl(anyString());

        when(environmentReaderMock.getMandatoryString(anyString()))
            .thenReturn("http://localhost:4082")
            .thenReturn("apiKeyForTesting")
            .thenReturn("dev-pdf-bucket/chs-dev");

        doReturn(false).when(environmentReaderMock)
            .getMandatoryBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR);

        //TODO - uncomment when api-enumerations  has been added to the project
        /*when(documentDescriptionHelperMock.getDescription(anyString(), any(Map.class)))
            .thenReturn("Small full accounts made up to " + PREVIOUS_PERIOD_END_ON);*/

        Filing filing = filingService.generateAccountFiling(transaction, companyAccountEntity);

        verifyDocumentGeneratorCallerMock();
        verifyEnvReaderGetMandatoryString(DOCUMENT_RENDER_SERVICE_HOST_ENV_VAR);

        verify(environmentReaderMock, times(1))
            .getMandatoryBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR);

        verifyFilingData(filing);
    }

    @DisplayName("Tests the filing not generated when small full accounts link is not present")
    @Test
    void shouldNotGenerateFilingAsSmallFullLinkNotPresentWithinAccountData()
        throws IOException {

        companyAccountEntity.getData().setLinks(new HashMap<>());
        Filing filing = filingService.generateAccountFiling(transaction, companyAccountEntity);

        assertNull(filing);
    }


    @DisplayName("Tests the filing not generated when ixbrl has not been generated, ixbrl location not set")
    @Test
    void shouldNotGenerateFilingAsIxbrlLocationNotSet()
        throws IOException {

        when(documentGeneratorCallerMock.generateIxbrl(anyString()))
            .thenReturn(null);

        when(environmentReaderMock.getMandatoryString(anyString()))
            .thenReturn("http://localhost:4082")
            .thenReturn("apiKeyForTesting")
            .thenReturn("dev-pdf-bucket/chs-dev");

        Filing filing = filingService.generateAccountFiling(transaction, companyAccountEntity);

        verifyDocumentGeneratorCallerMock();
        verifyEnvReaderGetMandatoryString(DOCUMENT_RENDER_SERVICE_HOST_ENV_VAR);

        assertNull(filing);
    }

    /**
     * Check environmentReaderMock is called the passed-in number of times and the argument's values
     * of these calls matches the passed-in args.
     *
     * @param args the values the environment reader is called with.
     */
    private void verifyEnvReaderGetMandatoryString(String... args) {

        verify(environmentReaderMock, times(args.length))
            .getMandatoryString(argCaptor.capture());

        List<String> capturedEnvVariables = argCaptor.getAllValues();
        Set<String> expectedValues = new HashSet<>(Arrays.asList(args));

        assertTrue(
            capturedEnvVariables
                .stream().map(String::toString)
                .collect(Collectors.toSet())
                .equals(expectedValues));
    }

    private void verifyObjectMapperNumOfCalls() throws JsonProcessingException {
        verify(objectMapperMock, times(1)).writeValueAsString(any(Object.class));
    }

    private void verifyDocumentGeneratorCallerMock() throws IOException {
        verify(documentGeneratorCallerMock, times(1))
            .generateIxbrl(anyString());
    }

    /**
     * Check the returned filing matches the expected filing's data.
     *
     * @param filing
     */
    private void verifyFilingData(Filing filing) {
        assertNotNull(filing);

        assertEquals(COMPANY_NUMBER, filing.getCompanyNumber());
        assertEquals(AccountsType.SMALL_FULL_ACCOUNTS.getAccountType(),
            filing.getDescriptionIdentifier());

        //TODO - uncomment when api-enumerations  has been added to the project
        // assertEquals(FILINGS_DESCRIPTION, filing.getDescription());

        assertNotNull(filing.getDescriptionValues());
        assertNotNull(filing.getDescriptionValues().get(PERIOD_END_ON_KEY));
        assertEquals(AccountsType.SMALL_FULL_ACCOUNTS.getKind(), filing.getKind());

        Data data = filing.getData();
        assertNotNull(data);
        assertNotNull(data.getPeriodEndOn());

        List<Link> links = data.getLinks();
        assertNotNull(links);
        assertEquals(1, links.size());

        Link link = links.get(0);
        assertNotNull(link);
        assertEquals(LINK_RELATIONSHIP, link.getRelationship());
        assertEquals(IXBRL_LOCATION, link.getHref());
    }

    /**
     * Builds transaction object.
     *
     * @return
     * @throws ParseException
     */
    private Transaction createTransaction() throws ParseException {
        Transaction transaction = new Transaction();

        transaction.setId(TRANSACTION_ID);
        transaction.setClosedAt(getDateFormatted("Friday, Jul 13, 2018 14:04:56 PM"));
        transaction.setCompanyNumber(COMPANY_NUMBER);
        transaction.setKind("transaction");
        transaction.setStatus("closed");
        transaction.setUpdatedAt(getDateFormatted("Friday, Jul 13, 2018 14:02:56 PM"));
        transaction.setLinks(createTransactionLinks());

        return transaction;
    }

    /**
     * Builds Account Entity object.
     *
     * @return
     * @throws ParseException
     */
    private CompanyAccountEntity createAccountEntity() {
        CompanyAccountEntity account = new CompanyAccountEntity();

        account.setId(ACCOUNTS_ID);

        CompanyAccountDataEntity accountData = new CompanyAccountDataEntity();
        accountData.setEtag("etagForTesting");
        accountData.setKind("accounts");
        accountData.setLinks(createAccountEntityLinks());
        accountData.setPeriodEndOn(LocalDate.of(2018, 1, 1));
        account.setData(accountData);

        return account;
    }

    /**
     * Gets the links for the transaction. Self link.
     *
     * @return
     */
    private Map<String, String> createTransactionLinks() {
        Map<String, String> transactionLinks = new HashMap<>();
        transactionLinks.put(LinkType.SELF.getLink(), "/transactions/" + TRANSACTION_ID);
        return transactionLinks;
    }

    /**
     * Creates the links for the accounts data.
     *
     * @return
     */
    private Map<String, String> createAccountEntityLinks() {
        Map<String, String> dataLinks = new HashMap<>();

        dataLinks.put(LinkType.SELF.getLink(),
            String.format("/transactions/%s/company-accounts/%s", TRANSACTION_ID, ACCOUNTS_ID));

        dataLinks.put(LinkType.SMALL_FULL.getLink(),
            String.format("/transactions/%s/company-accounts/%s/small-full/%s",
                TRANSACTION_ID,
                ACCOUNTS_ID,
                SMALL_FULL_ID));

        return dataLinks;
    }

    private Date getDateFormatted(String dateInString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(TRANSACTION_DATE_FORMAT);
        Date date = formatter.parse(dateInString);

        return date;
    }

    /**
     * Builds the small full accounts model. Functionality needs to be change.
     */
    private Account getSmallFullAccount() {
        Account account = new Account();
        account.setPeriod(getAccountPeriod());
        account.setBalanceSheet(getBalanceSheet());
        account.setNotes(getNotes());
        account.setCompany(getCompany());

        return account;
    }

    private Company getCompany() {
        Company company = new Company();
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyNumber(COMPANY_NAME);

        return company;
    }

    private Notes getNotes() {
        Notes notes = new Notes();

        PostBalanceSheetEvents postBalanceSheetEvents = new PostBalanceSheetEvents();
        postBalanceSheetEvents.setCurrentPeriodDateFormatted(CURRENT_PERIOD_FORMATTED);
        postBalanceSheetEvents.setPostBalanceSheetEventsInfo("test post balance note");

        notes.setPostBalanceSheetEvents(postBalanceSheetEvents);

        return notes;
    }

    private Period getAccountPeriod() {
        Period period = new Period();
        period.setCurrentPeriodStartOn(CURRENT_PERIOD_START_ON);
        period.setCurrentPeriodEndsOn(CURRENT_PERIOD_END_ON);
        period.setPreviousPeriodStartOn(PREVIOUS_START_ON);
        period.setPreviousPeriodEndsOn(PREVIOUS_PERIOD_END_ON);

        return period;
    }

    /**
     * Build BalanceSheet model by using information from database
     */
    private BalanceSheet getBalanceSheet() {
        BalanceSheet balanceSheet = new BalanceSheet();

        CalledUpSharedCapitalNotPaid calledUpSharedCapitalNotPaid = new CalledUpSharedCapitalNotPaid();
        calledUpSharedCapitalNotPaid.setCurrentAmount(9);
        calledUpSharedCapitalNotPaid.setPreviousAmount(99);

        balanceSheet.setCalledUpSharedCapitalNotPaid(calledUpSharedCapitalNotPaid);
        balanceSheet.setCurrentPeriodDateFormatted(CURRENT_PERIOD_FORMATTED);
        balanceSheet.setPreviousPeriodDateFormatted(PREVIOUS_PERIOD_FORMATTED);

        return balanceSheet;
    }

    /**
     * Get file content.
     *
     * @param fileNamePath - contains file location and name.
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private String getFileContentFromResource(String fileNamePath)
        throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource(fileNamePath).toURI());
        return new String(Files.readAllBytes(path));
    }
}
