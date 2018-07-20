package uk.gov.companieshouse.api.accounts.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionManagerImpl implements TransactionManager {

    private RestTemplate transactionRestTemplate;

    @Autowired
    private TransactionServiceProperties configuration;

    private static final String ID_PARAMETER = "{id}";

    public TransactionManagerImpl(RestTemplateBuilder builder, TransactionServiceProperties configuration) {
        this.configuration = configuration;
        this.transactionRestTemplate = builder.rootUri(configuration.getRootUri())
                .basicAuthorization(configuration.getApiKey(), "").build();
    }

    /**
     * Try get transaction if exists
     *
     * @param id
     * @param requestId
     * @return transaction object along with the status or a Not Found status.
     */
    public ResponseEntity<Transaction> getTransaction(String id, String requestId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("x-request-id", requestId);
        HttpEntity requestEntity = new HttpEntity(requestHeaders);
        String url = (configuration.getBaseUrl()).replace(ID_PARAMETER, id);
        return transactionRestTemplate.exchange(url, HttpMethod.GET, requestEntity, Transaction.class);
    }
}