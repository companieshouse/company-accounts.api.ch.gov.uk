package uk.gov.companieshouse.api.accounts.interceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.transaction.request.TransactionsGet;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.sdk.apimanager.ApiSdkManager;

@Component
public class TransactionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    // @Autowired
    // private TransactionManager transactionManager;

    /**
     * Pre handle method to validate the request before it reaches the
     * controller. Check if the url has an existing transaction and save it in
     * the request's attribute. If transaction is not found then return 404
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("request_method", request.getMethod());

        try {
            Map<String, String> pathVariables = (Map) request
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            String transactionId = pathVariables.get("transactionId");
            // ResponseEntity<Transaction> transaction = transactionManager
            // .getTransaction(transactionId,
            // request.getHeader("X-Request-Id"));
            //
            // request.setAttribute(AttributeName.TRANSACTION.getValue(),
            // transaction.getBody());

            String passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

            // TODO remove from debugMap
            debugMap.put("passthrough_header", passthroughHeader);

            ApiClient apiClient = null;
            try {
                apiClient = ApiSdkManager.getSDK(passthroughHeader);
            } catch (IOException ioe) {
                LOGGER.errorRequest(request, ioe, debugMap);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return false;
            }

            TransactionsGet transactionsGet = apiClient.transactions().get("/transactions/" + transactionId);
            Transaction transaction = null;
            try {
                transaction = transactionsGet.execute();
            } catch (ApiErrorResponseException aer) {
                LOGGER.errorRequest(request, aer, debugMap);
                response.setStatus(aer.getStatusCode());
                return false;
            } catch (URIValidationException uve) {
                LOGGER.errorRequest(request, uve, debugMap);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return false;
            }

            request.setAttribute(AttributeName.TRANSACTION.getValue(), transaction);

            return true;
        } catch (HttpClientErrorException httpClientErrorException) {
            LOGGER.errorRequest(request, httpClientErrorException, debugMap);
            response.setStatus(httpClientErrorException.getStatusCode().value());
            return false;
        }
    }
}