package uk.gov.companieshouse.api.accounts.transaction;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.LinkType;

@Component
public class TransactionManagerImpl implements TransactionManager {

    @Autowired
    private TransactionServiceProperties configuration;

    private static final String ID_PARAMETER = "{id}";
    private static final String X_REQUEST_ID = "X-Request-Id";

    // represents the Authorization header name in the request
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final RestTemplate restTemplate = createRestTemplate();

    public TransactionManagerImpl(TransactionServiceProperties configuration) {
        this.configuration = configuration;
    }

    /**
     * Try get transaction if exists
     *
     * @param id - transaction id
     * @param requestId - id of the request
     * @return transaction object along with the status or not found status.
     */
    public ResponseEntity<Transaction> getTransaction(String id, String requestId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(X_REQUEST_ID, requestId);
        requestHeaders.set(AUTHORIZATION_HEADER, getApiKey());

        HttpEntity requestEntity = new HttpEntity(requestHeaders);
        String url = getBaseUrl(id);
        return getTransaction(url, requestEntity);
    }

    /**
     * Populates the request body and headers in which to update the transaction
     *
     * @param transactionId - transaction id
     * @param requestId - id of the request
     * @param link - link of the resource to add to the transaction resources
     */
    public void updateTransaction(String transactionId, String requestId, String link) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("resources", createResourceMap(link));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "merge-patch+json"));
        requestHeaders.set(X_REQUEST_ID, requestId);
        requestHeaders.set(AUTHORIZATION_HEADER, getApiKey());

        HttpEntity requestEntity = new HttpEntity(requestBody, requestHeaders);

        patchTransaction(getPatchUrl(transactionId), requestEntity);
    }

    /**
     * Creates the rest template when class first loads
     *
     * @return Returns a statically created rest template
     */
    private static RestTemplate createRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        return new RestTemplate(requestFactory);
    }

    /**
     * Gets the api key from the properties file
     *
     * @return Returns the api key if set, otherwise throw an exception
     */
    private String getApiKey() {
        String apiKey = configuration.getApiKey();
        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("API Key property has not been set");
        }
        return apiKey;
    }

    /**
     * Get the base request URL from the properties file
     *
     * @param transactionId - transaction id
     * @return Get the base url if set, otherwise throw an exception
     */
    private String getBaseUrl(String transactionId) {
        String url = configuration.getBaseUrl().replace(ID_PARAMETER, transactionId);
        if (url.isEmpty()) {
            throw new IllegalArgumentException("BaseURL property has not been set");
        }
        return url;
    }

    /**
     * Get the PATCH request URL from the properties file
     *
     * @param transactionId - transaction id
     * @return Get the patch url if set, otherwise throw an exception
     */
    private String getPatchUrl(String transactionId) {
        String url = configuration.getPatchUrl().replace(ID_PARAMETER, transactionId);
        if (url.isEmpty()) {
            throw new IllegalArgumentException("PatchURL property has not been set");
        }
        return url;
    }

    /**
     * Get the root uri from the properties file
     *
     * @return Get the root uri if set, otherwise throw an exception
     */
    private String getRootUri() {
        String url = configuration.getRootUri();
        if (url.isEmpty()) {
            throw new IllegalArgumentException("RootURL property has not been set");
        }
        return url;
    }

    /**
     * GET the transaction resource
     *
     * @param url - url to send the get request
     * @param requestEntity - the request entity object
     */
    private ResponseEntity<Transaction> getTransaction(String url, HttpEntity requestEntity) {
        return restTemplate.exchange(getRootUri() + url, HttpMethod.GET, requestEntity, Transaction.class);
    }

    /**
     * Patch the transaction resource
     *
     * @param url - url to send the get request
     * @param requestEntity - the request entity object
     */
    private void patchTransaction(String url, HttpEntity requestEntity) {
        restTemplate.exchange(getRootUri() + url, HttpMethod.PATCH, requestEntity, Void.class);
    }

    /**
     * Creates the resources map for the patching of the transaction
     *
     * @param link - the link in which to add to the resource
     */
    private Map createResourceMap(String link){
        Resources resource = new Resources();
        resource.setKind(Kind.COMPANY_ACCOUNTS.getValue());

        Map<String,String> links = new HashMap<>();
        links.put(LinkType.RESOURCE.getLink(), link);
        resource.setLinks(links);
        resource.setUpdatedAt(new Date());

        Map<String,Resources> resourceMap = new HashMap<>();
        resourceMap.put(link, resource);
        return resourceMap;
    }
}