package uk.gov.companieshouse.api.accounts.utility;

import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

public class LoggingHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);
    private static final String TRANSACTION_ID = "transaction_id";
    private static final String COMPANY_ACCOUNT_ID = "company_account_id";
    private static final String MESSAGE = "message";

    private LoggingHelper() {}

    public static void logException(String companyAccountId,
                                    Transaction transaction,
                                    String message,
                                    Exception exception,
                                    HttpServletRequest request) {
        LOGGER.errorRequest(request, exception, createDebugMap(companyAccountId, transaction, message));
    }

    private static Map<String, Object> createDebugMap(String companyAccountId,
                                                      Transaction transaction,
                                                      String message) {
        Map<String, Object> debugMap = new HashMap<>();
        debugMap.put(TRANSACTION_ID, transaction.getId());
        debugMap.put(COMPANY_ACCOUNT_ID, companyAccountId);
        debugMap.put(MESSAGE, message);
        return debugMap;
    }

    public static void logInfo(String companyAccountId,
                               Transaction transaction,
                               String message,
                               HttpServletRequest request) {
        LOGGER.infoRequest(request, message, createDebugMap(companyAccountId, transaction, message));
    }
}
