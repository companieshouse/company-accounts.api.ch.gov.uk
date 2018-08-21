package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionManager;
import uk.gov.companieshouse.api.accounts.transaction.TransactionStatus;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

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
            request.setAttribute(AttributeName.TRANSACTION.getValue(), transaction.getBody());

            return isValidTransaction(request, transaction);

        } catch (HttpClientErrorException httpClientErrorException) {
            response.setStatus(httpClientErrorException.getStatusCode().value());
            return false;
        }
    }

    /**
     * Check transaction is valid. e.g. Accounts api call needs the Transaction to be open. Filing
     * Generator call needs the transaction to be closed.
     *
     * @param request
     * @param transaction
     * @return
     */
    private boolean isValidTransaction(HttpServletRequest request,
        ResponseEntity<Transaction> transaction) {
        if (isFilingGeneratorRequest(request)) {
            return isTransactionClosed(transaction);
        } else {
            return isTransactionIsOpen(transaction);
        }
    }

    /**
     * Check if request is a Filing Generator request, ending in /filings.
     * @param request - http request.
     *
     * @return
     */
    private boolean isFilingGeneratorRequest(HttpServletRequest request) {
        return request.getRequestURI().endsWith("/filings");
    }

    /**
     * Returns whether transaction is open or not.
     */
    private boolean isTransactionIsOpen(ResponseEntity<Transaction> transaction) {
        return (transaction.getBody() != null && transaction.getBody().getStatus()
                .equals(TransactionStatus.OPEN.getStatus()));
    }

    /**
     * Returns whether transaction is open or not.
     */
    private boolean isTransactionClosed(ResponseEntity<Transaction> transaction) {
        return (transaction.getBody().getStatus().equals(TransactionStatus.CLOSED.getStatus()));
    }

}