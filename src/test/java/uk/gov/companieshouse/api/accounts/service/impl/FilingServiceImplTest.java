package uk.gov.companieshouse.api.accounts.service.impl;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Basic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ixbrl.DocumentGeneratorCaller;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class FilingServiceImplTest {

    private static final String TRANSACTION_ID = "1234561-1234561-1234561";
    private static final String ACCOUNTS_ID = "1234561";
    private static final String SMALL_FULL_ID = "smallFullId";
    private static final String IXBRL_LOCATION = "http://test/ixbrl_bucket_location";
    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";

    private FilingService filingService;
    private CompanyAccount companyAccount;
    private Transaction transaction;

    @Mock
    private DocumentGeneratorCaller documentGeneratorCallerMock;
    @Mock
    private EnvironmentReader environmentReaderMock;

    @BeforeEach
    void setUpBeforeEach() {
        transaction = new Transaction();
        companyAccount = createAccount();

        filingService = new FilingServiceImpl(documentGeneratorCallerMock, environmentReaderMock);
    }

    @Test
    @DisplayName("Tests the filing generation. Happy path")
    void shouldGenerateFiling() {
        doReturn(IXBRL_LOCATION).when(documentGeneratorCallerMock).generateIxbrl();
        doReturn(false).when(environmentReaderMock)
            .getMandatoryBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR);

        Filing filing = filingService
            .generateAccountFiling(transaction, companyAccount);

        verifyDocumentGeneratorCallerMock();
        verify(environmentReaderMock, times(1))
            .getMandatoryBoolean(DISABLE_IXBRL_VALIDATION_ENV_VAR);

        assertNotNull(filing);
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
        doReturn(null).when(documentGeneratorCallerMock).generateIxbrl();

        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);

        verifyDocumentGeneratorCallerMock();
        assertNull(filing);
    }

    private void verifyDocumentGeneratorCallerMock() {
        verify(documentGeneratorCallerMock, times(1)).generateIxbrl();
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
}