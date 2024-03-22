package uk.gov.companieshouse.api.accounts.interceptor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class OpenTransactionInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    /**
     * Pre handle method to validate the request before it reaches the controller by checking if the
     * request is a GET request and if the transaction's status is open.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        String requestMethod = request.getMethod();

        if (transaction == null ||
            (!requestMethod.equals("GET") && !TransactionStatus.OPEN.getStatus()
                .equalsIgnoreCase(transaction.getStatus().getStatus()))) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("request_method", request.getMethod());

            LOGGER.errorRequest(request, "OpenTransactionInterceptor error: no open transaction available",
                    debugMap);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}