package uk.gov.companieshouse.api.accounts.api;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;
import uk.gov.companieshouse.api.http.HttpClient;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Component
public class ApiClientServiceImpl implements ApiClientService {

    private static final String X_REQUEST_ID_HEADER = "x-request-id";
    private final String chsApiKey;
    private final String apiUrl;
    private final EnvironmentReader environmentReader;

    @Autowired
    public ApiClientServiceImpl(EnvironmentReader environmentReader) {
        this.environmentReader = environmentReader;

        chsApiKey = environmentReader.getMandatoryString("CHS_API_KEY");
        apiUrl = environmentReader.getMandatoryString("API_URL");
    }

    @Override
    public ApiClient getApiClient() {

        HttpClient httpClient = new ApiKeyHttpClient(chsApiKey);
        setRequestId(httpClient);

        ApiClient apiClient = new ApiClient(httpClient);
        apiClient.setBasePath(apiUrl);

        return apiClient;
    }

    /**
     * Set request ID using httpclient
     *
     * @param httpClient
     */
    private void setRequestId(HttpClient httpClient) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
            .currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();

        String requestId = (String) request.getAttribute(X_REQUEST_ID_HEADER);

        if (requestId == null)
            requestId = request.getHeader(X_REQUEST_ID_HEADER);

        if (requestId == null || requestId.isEmpty()) {
            requestId = generateRequestId();
            request.setAttribute(X_REQUEST_ID_HEADER, requestId);
        }

        httpClient.setRequestId(requestId);
    }

    /**
     * Generate a universally unique identifier
     *
     * @return
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 20);
    }
}