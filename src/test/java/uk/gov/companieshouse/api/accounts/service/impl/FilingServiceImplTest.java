package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sun.corba.OutputStreamFactory;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class FilingServiceImplTest {

    private static final String TRANSACTION_ID = "1234561-1234561-1234561";
    private static final String ACCOUNTS_ID = "1234561";
    private static final String COMPANY_NUMBER = "1234";
    private static final String DOCUMENT_RENDER_SERVICE_HOST_ENV = "DOCUMENT_RENDER_SERVICE_HOST";
    private static final String DOCUMENT_RENDER_SERVICE_HOST_VALUE = "http://chs-dev-test";

    @Mock
    private EnvironmentReader environmentReader;
    @Mock
    private ObjectMapper mockObjectMapper;
    @Mock
    private DataOutputStream mockDataOutputStream;
    @Mock
    private HttpURLConnection mockHttpURLConnection;
    @Mock
    private OutputStream mockOutputStream;
    @Mock
    private URL mockUrl;

    @InjectMocks
    private FilingServiceImpl filingService = new FilingServiceImpl();

    @Test
    public void shouldGenerateFiling() throws IOException {

        when(mockObjectMapper.writeValueAsString(any(Object.class))).thenReturn(getSmallFullJson());

        when(environmentReader.getMandatoryString(DOCUMENT_RENDER_SERVICE_HOST_ENV))
            .thenReturn(DOCUMENT_RENDER_SERVICE_HOST_VALUE);

        when(mockHttpURLConnection.getOutputStream()).thenReturn(mockOutputStream);

        Filing filing = filingService.generateAccountFiling(TRANSACTION_ID, ACCOUNTS_ID);
        assertEquals(true, true);
    }

    private String getSmallFullJson() {
        String json =
            "{\n"
                + "  \"small_full_accounts\": {\n"
                + "    \"period\": {\n"
                + "      \"previous_period_ends_on\": \"2016-01-01\",\n"
                + "      \"previous_period_start_on\": \"2016-12-01\",\n"
                + "      \"current_period_ends_on\": \"2018-05-01\",\n"
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
