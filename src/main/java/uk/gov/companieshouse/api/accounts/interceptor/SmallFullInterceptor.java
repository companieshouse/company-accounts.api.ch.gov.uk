package uk.gov.companieshouse.api.accounts.interceptor;

import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
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

    private static final Logger LOGGER = LoggerFactory.getLogger("company-accounts.api.ch.gov.uk");
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

        if (request.getMethod().equalsIgnoreCase("POST") && request.getRequestURI().endsWith("small-full") ) {
            return true;
        }

        HttpSession session = request.getSession();
        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) session
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        if (companyAccountEntity == null) {
            LOGGER.error(
                    "SmallFullInterceptor failed on preHandle: Failed to retrieve a companyAccountEntity from the session.");
            return false;
        }

        String companyAccountId = companyAccountEntity.getId();
        String smallFullId = smallFullService.generateID(companyAccountId);
        SmallFullEntity smallFull = smallFullService.findById(smallFullId);
        if (smallFull == null) {
            LOGGER.error(
                    "SmallFullInterceptor failed on preHandle: Failed to retrieve a SmallFull account from the Database.");
            return false;
        }

        String companyAccountLink = companyAccountEntity.getData().getLinks()
                .get(LinkType.SMALL_FULL.getLink());
        String smallFullSelf = smallFull.getData().getLinks().get(LinkType.SELF.getLink());
        if (!companyAccountLink.equals(smallFullSelf)) {
            LOGGER.error(
                    "SmallFullInterceptor failed on preHandle: The SmallFull self link does not exist in the CompanyAccounts links");
            return false;
        }

        session.setAttribute(AttributeName.SMALLFULL.getValue(), smallFull);
        return true;

    }
}