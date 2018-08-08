package uk.gov.companieshouse.api.accounts.interceptor;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * The SmallFullInterceptor interceptor validates that the Smallfull entity is in the correct state
 * for the calling request. It insures a Smallfull resource exists and matches the one stored in the
 * CompanyAccount provided in the session. This interceptor will fail if the CompanyAccount
 * interceptor has not run prior to its own execution.
 */
@Component
public class SmallFullInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    @Autowired
    private SmallFullService smallFullService;

    /**
     * This class validates a Small-full account exists for the given CompanyAccount. Validation is
     * carried out via a database lookup for the SmallFullEntity Object then matching the retrieved
     * Entities 'self' link to the session stored CompanyAccountEntities 'small_full_accounts' link.
     * Providing this validation passes it assigns the SmallFullEntity to the session.
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
            Object handler) throws NoSuchAlgorithmException {

        //The GET and POST mappings are the same URL, on a POST the small-full will not exist as yet
        //so we do not want to run this interceptor.
        if (request.getMethod().equalsIgnoreCase("POST") && request.getRequestURI()
                .endsWith("small-full")) {
            return true;
        }

        final Map<String, Object> debugMap = new HashMap<String, Object>();
        debugMap.put("request_method", request.getMethod());

        HttpSession session = request.getSession();
        Transaction transaction = (Transaction) session
                .getAttribute(AttributeName.TRANSACTION.getValue());
        if (transaction == null) {
            debugMap.put("message",
                    "SmallFullInterceptor error: No transaction in request session");
            LOGGER.errorRequest(request, null, debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        Map<String, String> pathVariables = (Map) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("transaction_company_number", transaction.getCompanyNumber());
        debugMap.put("path_variables", pathVariables);

        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) session
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        if (companyAccountEntity == null) {
            debugMap.put("message",
                    "SmallFullInterceptor error: No company account in request session");
            LOGGER.errorRequest(request, null, debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        String companyAccountId = companyAccountEntity.getId();
        String smallFullId = smallFullService.generateID(companyAccountId);
        SmallFullEntity smallFull;
        try {
            smallFull = smallFullService.findById(smallFullId);
        } catch (DataAccessException dae) {
            LOGGER.errorRequest(request, dae, debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        if (smallFull == null) {
            LOGGER.debugRequest(request,
                    "SmallFullInterceptor error: Failed to retrieve a SmallFull account.",
                    debugMap);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        String companyAccountLink = companyAccountEntity.getData().getLinks()
                .get(LinkType.SMALL_FULL.getLink());
        String smallFullSelf = smallFull.getData().getLinks().get(LinkType.SELF.getLink());
        if (!companyAccountLink.equals(smallFullSelf)) {
            LOGGER.debugRequest(request,
                    "SmallFullInterceptor error: The SmallFull self link does not exist in the CompanyAccounts links",
                    debugMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        session.setAttribute(AttributeName.SMALLFULL.getValue(), smallFull);
        return true;

    }
}
