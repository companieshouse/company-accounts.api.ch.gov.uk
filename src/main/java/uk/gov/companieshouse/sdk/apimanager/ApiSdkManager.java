package uk.gov.companieshouse.sdk.apimanager;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;
import uk.gov.companieshouse.api.http.HttpClient;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

public class ApiSdkManager {

    private static final String ERIC_PASSTHROUGH_TOKEN_HEADER = "ERIC-Access-Token";
    private static final String X_REQUEST_ID_HEADER           = "x-request-id";
    private static final String CHS_API_KEY                   = "CHS_API_KEY";
    private static final String API_URL                       = "API_URL";

    private static EnvironmentReader environmentReader = new EnvironmentReaderImpl();

    private ApiSdkManager() {
        // private constructor set up to prevent instantiation
    }

    /**
     * Returns an instance of the SDK using API Key authentication.
     *
     * @return ApiClient - Will always use {@link ApiKeyHttpClient}
     */
    public static ApiClient getSDK() {
        HttpClient httpClient = getHttpClient();

        ApiClient apiClient = new ApiClient(httpClient);
        apiClient.setBasePath(getApiUrl());

        return apiClient;
    }

    /**
     * Returns an instance of the SDK. Uses the passthroughHeader to forward
     * credentials from the initial call to subsequent API calls.
     *
     * @param passthroughHeader
     * @return ApiClient
     * @throws IOException
     *             if the passthroughHeader cannot be decoded
     */
    public static ApiClient getSDK(String passthroughHeader) throws IOException {
        PassthroughToken token = decodePassthroughHeader(passthroughHeader);
        HttpClient httpClient = getHttpClient(token);

        ApiClient apiClient = new ApiClient(httpClient);
        apiClient.setBasePath(getApiUrl());

        return apiClient;
    }

    /**
     * Returns an instance of the Private SDK using API Key authentication.
     *
     * @return InternalApiClient - Will always use {@link ApiKeyHttpClient}
     */
    public static InternalApiClient getPrivateSDK() {
        HttpClient httpClient = getHttpClient();

        InternalApiClient internalApiClient = new InternalApiClient(httpClient);
        internalApiClient.setBasePath(getApiUrl());

        return internalApiClient;
    }

    /**
     * Returns an instance of the Private SDK. Uses the passthroughHeader to
     * forward credentials from the initial call to subsequent API calls.
     *
     * @param passthroughHeader
     * @return InternalApiClient
     * @throws IOException
     *             if the passthroughHeader cannot be decoded
     */
    public static InternalApiClient getPrivateSDK(String passthroughHeader) throws IOException {
        PassthroughToken token = decodePassthroughHeader(passthroughHeader);
        HttpClient httpClient = getHttpClient(token);

        InternalApiClient internalApiClient = new InternalApiClient(httpClient);
        internalApiClient.setBasePath(getApiUrl());

        return internalApiClient;
    }

    private static HttpClient getHttpClient() {
        return getHttpClient(null);
    }

    private static HttpClient getHttpClient(PassthroughToken token) {
        HttpClient httpClient;

        if (token == null) {
            httpClient = new ApiKeyHttpClient(getCHSApiKey());
        } else {
            httpClient = new ApiKeyHttpClient(token.getAccessToken());
        }

        setRequestId(httpClient);
        return httpClient;
    }

    private static PassthroughToken decodePassthroughHeader(String passthroughHeader) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(passthroughHeader, PassthroughToken.class);
    }

    private static void setRequestId(HttpClient httpClient) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
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

    private static String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 20);
    }

    private static String getCHSApiKey() {
        return environmentReader.getMandatoryString(CHS_API_KEY);
    }

    private static String getApiUrl() {
        return environmentReader.getMandatoryString(API_URL);
    }

    public static String getXRequestIdHeader() {
        return X_REQUEST_ID_HEADER;
    }

    public static String getEricPassthroughTokenHeader() {
        return ERIC_PASSTHROUGH_TOKEN_HEADER;
    }
}
