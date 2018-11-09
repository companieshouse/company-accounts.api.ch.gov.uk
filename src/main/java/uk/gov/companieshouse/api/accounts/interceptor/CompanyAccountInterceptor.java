package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.TransactionLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Resources;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * The CompanyAccountInterceptor interceptor validates that the Company account is in the correct
 * state for the calling request. It insures a Company account resource exists against the
 * transaction, checks the account ID is the same as the one that is referenced in the relevant
 * Transaction. This interceptor will fail if the Transaction interceptor has not run prior to its
 * own execution.
 */
@Component
public class CompanyAccountInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);
    @Autowired
    private CompanyAccountService companyAccountService;


    /**
     * This method extracts the 'company_account' parameter passed via the URI then validates it.
     * Validation is carried out via a database lookup for a CompanyAccountEntity Object then
     * matching the retrieved Entities 'self' link to the session stored Transactions
     * 'company_account' link. Providing this validation passes it assigns the CompanyAccountEntity
     * to the session.
     *
     * @param request - current HTTP request
     * @param response - current HTTP response
     * @param handler - chosen handler to execute, for type and/or instance evaluation
     * @return true if the execution chain should proceed with the next interceptor or the handler
     * itself.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {

        String requestId = request.getHeader("X-Request-Id");

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("request_method", request.getMethod());
        debugMap.put("request_id", requestId);

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());
        if (transaction == null) {
            debugMap.put("message",
                "CompanyAccountInterceptor error: no transaction in request session");
            LOGGER.errorRequest(request, null, debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        Map<String, String> pathVariables = (Map) request
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String companyAccountId = pathVariables.get("companyAccountId");

        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("transaction_company_number", transaction.getCompanyNumber());
        debugMap.put("path_variables", pathVariables);

        if (companyAccountId == null) {
            LOGGER.debugRequest(request,
                "CompanyAccountInterceptor error: no company account id provided", debugMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        ResponseObject<CompanyAccount> responseObject;
        try {
            responseObject = companyAccountService.findById(companyAccountId, request);
        } catch (DataException de) {
            LOGGER.errorRequest(request, de, debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        if (!responseObject.getStatus().equals(ResponseStatus.FOUND)) {
            LOGGER.debugRequest(request,
                "CompanyAccountInterceptor error: Failed to retrieve a CompanyAccount.",
                debugMap);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        CompanyAccount companyAccount = responseObject.getData();

        String accountsSelf = companyAccount.getLinks().get(BasicLinkType.SELF.getLink());
        Map<String, Resources> resourcesList = transaction.getResources();
        if (!isLinkInResourceMap(resourcesList, accountsSelf)) {
            LOGGER.debugRequest(request,
                "CompanyAccountInterceptor failed on preHandle: Failed to find the CompanyAccount self link in the Transactions links",
                debugMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        request.setAttribute(AttributeName.COMPANY_ACCOUNT.getValue(), companyAccount);
        return true;
    }

    private boolean isLinkInResourceMap(Map<String, Resources> resourcesList, String accountsSelf) {
        for (Entry<String, Resources> entry : resourcesList.entrySet()) {
            Resources resources = entry.getValue();
            if (resources.getKind().equals(Kind.COMPANY_ACCOUNTS.getValue()) && resources.getLinks()
                .get(TransactionLinkType.RESOURCE.getLink()).equals(accountsSelf)) {
                return true;
            }
        }
        return false;
    }


}