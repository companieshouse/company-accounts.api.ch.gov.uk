package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.AccountsType;
import uk.gov.companieshouse.api.accounts.model.filing.Data;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.filing.Link;
import uk.gov.companieshouse.api.accounts.util.ixbrl.DocumentGeneratorConnection;
import uk.gov.companieshouse.api.accounts.util.ixbrl.IxbrlGenerator;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class FilingServiceImplTest {

    private static final String TRANSACTION_ID = "1234561-1234561-1234561";
    private static final String ACCOUNTS_ID = "1234561";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String IXBRL_LOCATION = "http://test/ixbrl_bucket_location";
    private static final String LINK_RELATIONSHIP = "accounts";
    private static final String PERIOD_END_ON_KEY = "period_end_on";
    private static final String DOCUMENT_RENDER_SERVICE_HOST_ENV_VAR = "DOCUMENT_RENDER_SERVICE_HOST";
    private static final String API_KEY_ENV_VAR = "CHS_API_KEY";
    private static final String DOCUMENT_BUCKET_NAME_ENV_VAR = "DOCUMENT_BUCKET_NAME";
    private static final String DISABLE_IXBRL_VALIDATION_ENV_VAR = "DISABLE_IXBRL_VALIDATION";

    @Mock
    private EnvironmentReader environmentReader;
    @Mock
    private ObjectMapper mockObjectMapper;
    @Mock
    private IxbrlGenerator mockIxbrlGenerator;
    @InjectMocks
    private FilingServiceImpl filingService = new FilingServiceImpl();

    @DisplayName("Tests the filing is generated without errors. Happy path")
    @Test
    void shouldGenerateFiling() throws IOException {
        when(mockObjectMapper.writeValueAsString(any(Object.class))).thenReturn(getSmallFullJson());
        when(mockIxbrlGenerator
            .generateIXBRL(any(DocumentGeneratorConnection.class))).thenReturn(IXBRL_LOCATION);

        Filing filing = filingService.generateAccountFiling(TRANSACTION_ID, ACCOUNTS_ID);

        verifyObjectMapperNumOfCalls();
        verifyIxbrlGeneratorNumOfCalls();
        verifyEnvironmentReaderNumOfCalls(4,
            DOCUMENT_RENDER_SERVICE_HOST_ENV_VAR,
            API_KEY_ENV_VAR,
            DOCUMENT_BUCKET_NAME_ENV_VAR,
            DISABLE_IXBRL_VALIDATION_ENV_VAR);

        verifyFilingData(filing);
    }

    @DisplayName("Tests the filing is null when ixbrl location has not been set, ixbrl not generated")
    @Test
    void shouldNotGenerateFilingIxbrlLocationNotSet() throws IOException {
        when(mockObjectMapper.writeValueAsString(any(Object.class))).thenReturn(getSmallFullJson());

        Filing filing = filingService.generateAccountFiling(TRANSACTION_ID, ACCOUNTS_ID);
        verifyObjectMapperNumOfCalls();
        verifyIxbrlGeneratorNumOfCalls();
        verifyEnvironmentReaderNumOfCalls(3,
            DOCUMENT_RENDER_SERVICE_HOST_ENV_VAR,
            API_KEY_ENV_VAR,
            DOCUMENT_BUCKET_NAME_ENV_VAR);

        assertNull(filing);
    }

    @DisplayName("Tests the document render service unavailability - throw exception expected")
    @Test
    void shouldThrowExceptionDocumentGeneratorIsUnavailable() throws IOException {
        when(mockObjectMapper.writeValueAsString(any(Object.class))).thenReturn(getSmallFullJson());
        when(mockIxbrlGenerator
            .generateIXBRL(any(DocumentGeneratorConnection.class)))
            .thenThrow(new ConnectException());

        assertThrows(ConnectException.class,
            () -> filingService.generateAccountFiling(TRANSACTION_ID, ACCOUNTS_ID));
    }

    /**
     * Check environmentReader is called the passed-in number of times and the argument's value of
     * these calls matches the passed-in args.
     *
     * @param numOfTimes The number of times environment reader is be called.
     * @param args the values the environment reader is called with.
     */
    private void verifyEnvironmentReaderNumOfCalls(int numOfTimes, String... args) {
        ArgumentCaptor<String> envReaderCaptor = ArgumentCaptor.forClass(String.class);

        verify(environmentReader, times(numOfTimes)).getMandatoryString(envReaderCaptor.capture());

        List<String> capturedEnvVariables = envReaderCaptor.getAllValues();
        Set<String> expectedValues = new HashSet<>(Arrays.asList(args));

        assertTrue(capturedEnvVariables.stream().map(String::toString)
            .collect(Collectors.toSet())
            .equals(expectedValues));
    }

    private void verifyObjectMapperNumOfCalls() throws JsonProcessingException {
        verify(mockObjectMapper, times(1)).writeValueAsString(any(Object.class));
    }

    private void verifyIxbrlGeneratorNumOfCalls() throws IOException {
        verify(mockIxbrlGenerator, times(1)).
            generateIXBRL(any(DocumentGeneratorConnection.class));
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

        // TODO: filing.getDescription() is not possible to mock with Mockito. It static method. Description = null. Any ideas??
        // assertEquals(DESCRIPTION, filing.getDescription());
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

    private String getSmallFullJson() {
        String json =
            "{\n"
                + "  \"small_full_accounts\": {\n"
                + "    \"period\": {\n"
                + "      \"previous_period_end_on\": \"2016-01-01\",\n"
                + "      \"previous_period_start_on\": \"2016-12-01\",\n"
                + "      \"current_period_end_on\": \"2018-05-01\",\n"
                + "      \"current_period_start_on\": \"2017-05-01\"\n"
                + "    },\n"
                + "    \"notes\": {\n"
                + "      \"post_balance_sheet_events\": {\n"
                + "        \"current_period_date_formatted\": \"16 December 2017\",\n"
                + "        \"post_balance_sheet_events_info\": \"test post balance note\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"balance_sheet\": {\n"
                + "      \"current_period_date_formatted\": \"16 December 2017\",\n"
                + "      \"called_up_shared_capital_not_paid\": {\n"
                + "        \"current_amount\": 9,\n"
                + "        \"previous_amount\": 99\n"
                + "      },\n"
                + "      \"previous_period_date_formatted\": \"16 December 2017\"\n"
                + "    },\n"
                + "    \"company\": {\n"
                + "      \"company_number\": \"MYRETON RENEWABLE ENERGY LIMITED\",\n"
                + "      \"company_name\": \"SC344891\"\n"
                + "    }\n"
                + "  }\n"
                + "}";

        return json;
    }
}
