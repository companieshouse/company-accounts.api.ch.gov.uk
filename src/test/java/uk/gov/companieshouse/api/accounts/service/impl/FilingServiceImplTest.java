package uk.gov.companieshouse.api.accounts.service.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ixbrl.DocumentGeneratorCaller;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class FilingServiceImplTest {

    private static final String COMPANY_NUMBER = "9999999";
    private static final String TRANSACTION_ID = "1234561-1234561-1234561";
    private static final String ACCOUNTS_ID = "1234561";
    private static final String SMALL_FULL_ID = "smallFullId";
    private static final String IXBRL_LOCATION = "http://test/ixbrl_bucket_location";
    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";

    private static final String ACCOUNTS_SELF_REF =
        "/transactions/" + TRANSACTION_ID + "/company-accounts/" + ACCOUNTS_ID;

    private static final String PERIOD_END_ON_KEY = "period_end_on";
    private static final String ACCOUNT_DESCRIPTION = "Small full accounts made up to 18 January 2018";
    private static final String LINKS_RELATIONSHIP = "accounts";

    private FilingService filingService;
    private CompanyAccount companyAccount;
    private Transaction transaction;
    private DocumentGeneratorResponse documentGeneratorResponse;

    @Mock
    private DocumentGeneratorCaller documentGeneratorCallerMock;
    @Mock
    private EnvironmentReader environmentReaderMock;

    @BeforeEach
    void setUpBeforeEach() {
        transaction = createTransaction();
        companyAccount = createAccount();

        filingService = new FilingServiceImpl(documentGeneratorCallerMock, environmentReaderMock);
    }

    @Test
    @DisplayName("Tests the filing generation. Happy path")
    void shouldGenerateFiling() {
        documentGeneratorResponse = createDocumentGeneratorResponse(true, true, true);

        doReturn(documentGeneratorResponse)
            .when(documentGeneratorCallerMock).callDocumentGeneratorService(TRANSACTION_ID, ACCOUNTS_SELF_REF);

        doReturn(false).when(environmentReaderMock)
            .getMandatoryBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR);

        Filing filing = filingService
            .generateAccountFiling(transaction, companyAccount);

        verifyDocumentGeneratorCallerMock();
        verify(environmentReaderMock, times(1))
            .getMandatoryBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR);

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
    @DisplayName("Tests the filing not generated when ixbrl has not been generated, ixbrl location not set")
    void shouldNotGenerateFilingAsIxbrlLocationNotSet() {

        documentGeneratorResponse = createDocumentGeneratorResponse(false, true, true);
        doReturn(documentGeneratorResponse).when(documentGeneratorCallerMock)
            .callDocumentGeneratorService(TRANSACTION_ID, ACCOUNTS_SELF_REF);

        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);

        verifyDocumentGeneratorCallerMock();
        assertNull(filing);
    }

    @Test
    @DisplayName("Tests the filing not generated when document generator response fails")
    void shouldNotGenerateFilingAsDocumentGeneratorResposeIsNull() {

        doReturn(null).when(documentGeneratorCallerMock)
            .callDocumentGeneratorService(TRANSACTION_ID, ACCOUNTS_SELF_REF);

        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);

        verifyDocumentGeneratorCallerMock();
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
        assertEquals(ACCOUNT_DESCRIPTION, filing.getDescription());
        assertEquals(AccountsType.SMALL_FULL_ACCOUNTS.getKind(), filing.getKind());
        Assert.assertNotNull(filing.getDescriptionValues());
        Assert.assertNotNull(filing.getDescriptionValues().get(PERIOD_END_ON_KEY));

        Data data = filing.getData();
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getPeriodEndOn());

        List<Link> links = data.getLinks();
        Assert.assertNotNull(links);
        assertEquals(1, links.size());

        Link link = links.get(0);
        Assert.assertNotNull(link);
        assertEquals(LINKS_RELATIONSHIP, link.getRelationship());
        assertEquals(IXBRL_LOCATION, link.getHref());
    }

    private void verifyDocumentGeneratorCallerMock() {
        verify(documentGeneratorCallerMock, times(1))
            .callDocumentGeneratorService(TRANSACTION_ID, ACCOUNTS_SELF_REF);
    }

    /**
     * Builds Account Entity object.
     *
     * @return
     * @throws ParseException
     */
    private CompanyAccount createAccount() {
        CompanyAccount companyAccount = new CompanyAccount();

        companyAccount.setEtag("etagForTesting");
        companyAccount.setKind("accounts");
        companyAccount.setLinks(createAccountEntityLinks());
        companyAccount.setPeriodEndOn(LocalDate.of(2018, 1, 1));

        return companyAccount;
    }

    /**
     * Creates the links for the accounts data.
     *
     * @return
     */
    private Map<String, String> createAccountEntityLinks() {
        Map<String, String> dataLinks = new HashMap<>();

        dataLinks.put(BasicLinkType.SELF.getLink(),
            String.format("/transactions/%s/company-accounts/%s", TRANSACTION_ID, ACCOUNTS_ID));

        dataLinks.put(CompanyAccountLinkType.SMALL_FULL.getLink(),
            String.format("/transactions/%s/company-accounts/%s/small-full/%s",
                TRANSACTION_ID,
                ACCOUNTS_ID,
                SMALL_FULL_ID));

        return dataLinks;
    }

    /**
     * Create an transaction object
     *
     * @return
     * @throws ParseException
     */
    private Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);
        transaction.setCompanyNumber(COMPANY_NUMBER);
        return transaction;
    }

    /**
     * Create a Document Generator Response with all needed information to generate the filing.
     *
     * @return
     */
    private DocumentGeneratorResponse createDocumentGeneratorResponse(
        boolean addIxbrlLink, boolean addPeridEndValueToDescriptionValues,
        boolean addDescription) {

        DocumentGeneratorResponse documentGeneratorResponse = new DocumentGeneratorResponse();

        if (addIxbrlLink) {
            Links links = new Links();
            links.setLocation(IXBRL_LOCATION);
            documentGeneratorResponse.setLinks(links);
        }

        if (addPeridEndValueToDescriptionValues) {
            Map<String, String> descriptionValues = new HashMap<>();
            descriptionValues.put(PERIOD_END_ON_KEY, "2018-01-01");
            documentGeneratorResponse.setDescriptionValues(descriptionValues);
        }

        if (addDescription) {
            documentGeneratorResponse
                .setDescription("Small full accounts made up to 18 January 2018");
        }

        documentGeneratorResponse.setDescriptionIdentifier("small-full-accounts");
        documentGeneratorResponse.setSize("999999");

        return documentGeneratorResponse;
    }

}