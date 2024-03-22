package uk.gov.companieshouse.api.accounts.utility.ixbrl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.DocumentGeneratorRequest;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.DocumentGeneratorResponse;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Class to call the Document Generator to get the information needed by the Filing Generator to
 * build the filing object: the ixbrl location, period end on and description.
 */
@Component
public class DocumentGeneratorCaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String MIME_TYPE = "text/html";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${documentgenerator.service.host}")
    private String documentGeneratorHost;

    @Value("${documentgenerator.endpoint}")
    private String documentGeneratorEndPoint;

    private final RestTemplate restTemplate;

    private final EnvironmentReader environmentReader;

    @Autowired
    DocumentGeneratorCaller(RestTemplate restTemplate, EnvironmentReader environmentReader) {
        this.restTemplate = restTemplate;
        this.environmentReader = environmentReader;
    }

    public DocumentGeneratorResponse callDocumentGeneratorService(String accountsResourceUri) {
        DocumentGeneratorResponse documentGeneratorResponse = null;

        try {
            LOGGER.info("DocumentGeneratorCaller: Calling the document generator");

            ResponseEntity<DocumentGeneratorResponse> response =
                restTemplate.postForEntity(
                    getDocumentGeneratorURL(),
                    createHttpEntity(accountsResourceUri),
                    DocumentGeneratorResponse.class);

            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                documentGeneratorResponse = response.getBody();
                LOGGER.info("DocumentGeneratorCaller: Document generator call was successful");
            } else {
                final Map<String, Object> debugMap = new HashMap<>();
                debugMap.put("accounts id", accountsResourceUri);
                LOGGER.error("DocumentGeneratorCaller: wrong code returned from Document Generator",
                    debugMap);
            }

        } catch (RestClientException e) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("accounts id", accountsResourceUri);
            LOGGER.error(
                "DocumentGeneratorCaller: Exception occurred when calling the Document Generator",
                e, debugMap);
        }

        return documentGeneratorResponse;
    }

    /**
     * Create Document Generator Request object with the needed information to call the document
     * generator
     *
     * @param accountsResourceUri - the accounts self link.
     * @return
     */
    private DocumentGeneratorRequest createDocumentGeneratorRequest(String accountsResourceUri) {
        DocumentGeneratorRequest request = new DocumentGeneratorRequest();
        request.setResourceUri(accountsResourceUri);
        request.setMimeType(MIME_TYPE);

        return request;
    }

    private String getDocumentGeneratorURL() {
        return documentGeneratorHost + documentGeneratorEndPoint;
    }

    /**
     * Create httpEntity object containing the needed information to call the document generator:
     * Authorization header and DocumentGeneratorRequest.
     *
     * @param accountsResourceUri - the accounts self link.
     * @return {@link HttpEntity}
     */
    private HttpEntity createHttpEntity(String accountsResourceUri) {
        MultiValueMap<String, String> headers = createHeaders();
        DocumentGeneratorRequest request = createDocumentGeneratorRequest(accountsResourceUri);

        return new HttpEntity<>(request, headers);
    }

    /**
     * Add the authorization header that will be used to call the document generator.
     *
     * @return {@link MultiValueMap}
     */
    private MultiValueMap<String, String> createHeaders() {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, getApiKey());
        return headers;
    }

    /**
     * Gets the api key from the properties file.
     *
     * @return Returns the api key if set, otherwise throw an exception
     */
    private String getApiKey() {
        String apiKey = environmentReader.getMandatoryString("CHS_API_KEY");
        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("DocumentGeneratorCaller: API Key property has not been set");
        }
        return apiKey;
    }
}
