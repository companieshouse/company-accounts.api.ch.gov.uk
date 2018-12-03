package uk.gov.companieshouse.api.accounts.utility.ixbrl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.DocumentGeneratorRequest;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.DocumentGeneratorResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Class to call the document generator to obtain the ixbrl location. Since the functionality has
 * not been implemented yet, (STORY SFA-595), it returns an empty string.
 *
 * This class will change to call the new end point and it will return the s3 ixbrl location.
 */
@Component
public class DocumentGeneratorCaller {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String MIME_TYPE = "text/html";

    @Value("${documentgenerator.service.host}")
    private String documentGeneratorHost;

    @Value("${documentgenerator.endpoint}")
    private String documentGeneratorEndPoint;

    private RestTemplate restTemplate;

    @Autowired
    public DocumentGeneratorCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DocumentGeneratorResponse callDocumentGeneratorService(String transactionId,
        String accountsResourceUri) {

        DocumentGeneratorResponse documentGeneratorResponse = null;

        try {

            DocumentGeneratorRequest request =
                createDocumentGeneratorRequest(transactionId, accountsResourceUri);

            LOGGER.info("DocumentGeneratorCaller: Calling the document generator");
            ResponseEntity<DocumentGeneratorResponse> response =
                restTemplate.postForEntity(
                    getDocumentGeneratorURL(),
                    request,
                    DocumentGeneratorResponse.class);

            LOGGER.info("DocumentGeneratorCaller: Document generator call was successful");

            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                documentGeneratorResponse = response.getBody();

            } else {
                final Map<String, Object> debugMap = new HashMap<>();
                debugMap.put("transaction id", transactionId);
                debugMap.put("accounts id", accountsResourceUri);
                LOGGER.error("DocumentGeneratorCaller: wrong code returned from Document Generator",
                    debugMap);
            }

        } catch (RestClientException e) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction id", transactionId);
            debugMap.put("accounts id", accountsResourceUri);
            LOGGER.error(
                "DocumentGeneratorCaller: Exception occurred when calling the Document Generator",
                e, debugMap);
        }

        return documentGeneratorResponse;
    }

    private DocumentGeneratorRequest createDocumentGeneratorRequest(String transactionId,
        String accountsResourceUri) {
        DocumentGeneratorRequest request = new DocumentGeneratorRequest();
        request.setResourceUri(accountsResourceUri);
        request.setResourceID(transactionId);
        request.setMimeType(MIME_TYPE);
        return request;
    }

    private String getDocumentGeneratorURL() {
        return documentGeneratorHost + documentGeneratorEndPoint;
    }
}
