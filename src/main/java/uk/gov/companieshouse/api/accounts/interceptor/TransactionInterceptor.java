package uk.gov.companieshouse.api.accounts.interceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.sdk.ApiClientService;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Component
public class TransactionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    @Autowired
    private ApiClientService apiClientService;

    /**
     * Pre handle method to validate the request before it reaches the controller. Check if the url
     * has an existing transaction and save it in the request's attribute. If transaction is not
     * found then return 404
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("request_method", request.getMethod());

        try {
            Map<String, String> pathVariables = (Map) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            String transactionId = pathVariables.get("transactionId");
            String passthroughHeader = request
                .getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

            ApiClient apiClient = apiClientService.getApiClient(passthroughHeader);

            Transaction transaction = apiClient.transactions().get("/transactions/" + transactionId)
                .execute();

            request.setAttribute(AttributeName.TRANSACTION.getValue(), transaction);
            return true;

        } catch (HttpClientErrorException e) {

            LOGGER.errorRequest(request, e, debugMap);
            response.setStatus(e.getStatusCode().value());
            return false;

        } catch (ApiErrorResponseException e) {

            LOGGER.errorRequest(request, e, debugMap);
            response.setStatus(e.getStatusCode());
            return false;

        } catch (URIValidationException | IOException e) {

            LOGGER.errorRequest(request, e, debugMap);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return false;
        }
    }
}