package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionStatus;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class OpenTransactionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    /**
     * Pre handle method to validate the request before it reaches the controller by checking if
     * transaction's status is open.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        if (transaction == null) {
            logRequestError(request);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        return TransactionStatus.OPEN.getStatus().equals(transaction.getStatus());
    }

    /**
     * Log error.
     *
     * @param request
     */
    private void logRequestError(HttpServletRequest request) {
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("request_method", request.getMethod());
        debugMap
            .put("message", "OpenTransactionInterceptor error: no transaction in request session");
        LOGGER.errorRequest(request, null, debugMap);
    }
}