package uk.gov.companieshouse.api.accounts.sdk;

import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;

import java.io.IOException;

/**
 * The {@code ApiClientService} interface provides an abstraction that can be
 * used when testing {@code ApiSdkManager} static methods, without imposing
 * the use of a test framework that supports mocking of static methods.
 */
public interface ApiClientService {

    ApiClient getApiClient();
    ApiClient getApiClient(String passthroughHeader) throws IOException;

    InternalApiClient getInternalApiClient();
    InternalApiClient getInternalApiClient(String passthroughHeader) throws IOException;
}
