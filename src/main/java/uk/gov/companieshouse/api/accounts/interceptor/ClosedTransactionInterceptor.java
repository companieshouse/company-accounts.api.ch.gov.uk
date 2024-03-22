package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class ClosedTransactionInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    /**
     * Pre handle method to validate the request before it reaches the controller by checking if
     * transaction's status is closed.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        if (transaction == null || !TransactionStatus.CLOSED.getStatus()
            .equalsIgnoreCase(transaction.getStatus().getStatus())) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("request_method", request.getMethod());
            LOGGER.errorRequest(request,
                    "ClosedTransactionInterceptor error: no closed transaction available", debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
        return true;
    }
}