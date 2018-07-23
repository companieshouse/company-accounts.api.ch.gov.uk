package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionManager;
import uk.gov.companieshouse.api.accounts.transaction.TransactionStatus;

@Component
public class TransactionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private TransactionManager transactionManager;

    /**
     * Pre handle method to validate the request before it reaches the controller. Check if the url
     * has an existing transaction and to further check if transaction is open. If transaction is
     * not found then return 404
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {
        try {
            Map<String, String> pathVariables = (Map) request
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String transactionId = pathVariables.get("transactionId");
            ResponseEntity<Transaction> transaction = transactionManager
                    .getTransaction(transactionId, request.getHeader("X-Request-Id"));
            return isTransactionIsOpen(transaction);
        } catch (HttpClientErrorException httpClientErrorException) {
            response.setStatus(httpClientErrorException.getStatusCode().value());
            return false;
        }
    }

    /**
     * Returns whether transaction is open or not.
     */
    private boolean isTransactionIsOpen(ResponseEntity<Transaction> transaction) {
        return (transaction.getBody().getStatus().equals(TransactionStatus.OPEN.getStatus()));
    }
}