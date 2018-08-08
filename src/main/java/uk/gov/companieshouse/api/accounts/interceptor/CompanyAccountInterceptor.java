package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
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

    private static final Logger LOGGER = LoggerFactory.getLogger("company-accounts.api.ch.gov.uk");
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
        HttpSession session = request.getSession();
        Transaction transaction = (Transaction) session
                .getAttribute(AttributeName.TRANSACTION.getValue());
        if (transaction == null) {
            LOGGER.error(
                    "CompanyAccountInterceptor failed on preHandle: Failed to retrieve a transaction from the session.");
            return false;
        }

        Map<String, String> pathVariables = (Map) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String companyAccountId = pathVariables.get("companyAccountId");
        CompanyAccountEntity companyAccountEntity = companyAccountService
                .findById(companyAccountId);
        if (companyAccountEntity == null) {
            LOGGER.error(
                    "CompanyAccountInterceptor failed on preHandle: Failed to retrieve a CompanyAccount for "
                            + transaction
                            .getId() + ".");
            return false;
        }

        String accountsSelf = companyAccountEntity.getData().getLinks()
                .get(LinkType.SELF.getLink());
        Map<String, Resources> resourcesList = transaction.getResources();
        if (!isLinkInResourceMap(resourcesList, accountsSelf)) {
            LOGGER.error(
                    "CompanyAccountInterceptor failed on preHandle: Failed to find the CompanyAccount self link in the Transactions links "
                            + transaction
                            .getId() + ".");
            return false;
        }

        session.setAttribute(AttributeName.COMPANY_ACCOUNT.getValue(),
                companyAccountEntity);
        return true;
    }

    private boolean isLinkInResourceMap(Map<String, Resources> resourcesList, String accountsSelf) {
        for (Entry<String, Resources> entry : resourcesList.entrySet()) {
            Resources resources = entry.getValue();
            if (resources.getKind().equals(Kind.COMPANY_ACCOUNTS.getValue()) && resources.getLinks()
                    .get(LinkType.RESOURCE.getLink()).equals(accountsSelf)) {
                return true;
            }
        }
        return false;
    }
}