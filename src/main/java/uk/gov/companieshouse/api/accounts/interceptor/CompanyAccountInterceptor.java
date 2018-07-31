package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@Component
public class CompanyAccountInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private CompanyAccountService companyAccountService;

    /**
     * This class extracts the 'company_account' parameter passed via the URI then validates it.
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
        Map<String, String> pathVariables = (Map) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String companyAccountId = pathVariables.get("companyAccountId");
        CompanyAccountEntity companyAccountEntity = companyAccountService
                .findById(companyAccountId);
        Transaction transaction = (Transaction) request.getSession()
                .getAttribute(AttributeName.TRANSACTION.getValue());
        HttpSession session = request.getSession();
        if (transaction != null && companyAccountEntity != null) {
            String accountsLink = transaction.getLinks().get("company_account");
            String accountsSelf = companyAccountEntity.getData().getLinks().get("self");
            if (accountsLink.equals(accountsSelf)) {
                session.setAttribute(AttributeName.COMPANY_ACCOUNT.getValue(),
                        companyAccountEntity);
                return true;
            }
        }
        return false;
    }
}