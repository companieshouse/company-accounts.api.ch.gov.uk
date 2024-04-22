package uk.gov.companieshouse.api.accounts.interceptor;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * The SmallFullInterceptor interceptor validates that the Smallfull entity is in the correct state
 * for the calling request. It insures a Smallfull resource exists and matches the one stored in the
 * CompanyAccount provided in the session. This interceptor will fail if the CompanyAccount
 * interceptor has not run prior to its own execution.
 */
@Component
public class SmallFullInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

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
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws NoSuchAlgorithmException {

        //The GET and POST mappings are the same URL, on a POST the small-full will not exist as yet
        //so we do not want to run this interceptor.
        if (request.getMethod().equalsIgnoreCase("POST") && request.getRequestURI()
            .endsWith("small-full")) {
            return true;
        }

        String requestId = request.getHeader("X-Request-Id");

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("request_method", request.getMethod());
        debugMap.put("request_id", requestId);

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());
        if (transaction == null) {
             LOGGER.errorRequest(request, "SmallFullInterceptor error: No transaction in request session", debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        Map<String, String> pathVariables = (Map) request
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String companyAccountId = pathVariables.get("companyAccountId");

        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("transaction_company_number", transaction.getCompanyNumber());
        debugMap.put("path_variables", pathVariables);

        CompanyAccount companyAccount = (CompanyAccount) request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        if (companyAccount == null) {

            LOGGER.errorRequest(request,
                    "SmallFullInterceptor error: No company account in request session", debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        ResponseObject<SmallFull> responseObject;
        try {
            responseObject = smallFullService.find(companyAccountId, request);
        } catch (DataException de) {
            LOGGER.errorRequest(request, de, debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        if (!responseObject.getStatus().equals(ResponseStatus.FOUND)) {
            LOGGER.debugRequest(request,
                "SmallFullInterceptor error: Failed to retrieve a SmallFull account.",
                debugMap);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        SmallFull smallFull = responseObject.getData();

        String companyAccountLink = companyAccount.getLinks().get(CompanyAccountLinkType.SMALL_FULL.getLink());
        String smallFullSelf = smallFull.getLinks().get(SmallFullLinkType.SELF.getLink());
        if (!companyAccountLink.equals(smallFullSelf)) {
            LOGGER.debugRequest(request,
                "SmallFullInterceptor error: The SmallFull self link does not exist in the CompanyAccounts links",
                debugMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        request.setAttribute(AttributeName.SMALLFULL.getValue(), smallFull);
        return true;

    }
}
