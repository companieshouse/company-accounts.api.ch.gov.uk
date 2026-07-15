package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.exception.PermissionException;
import uk.gov.companieshouse.api.util.security.AuthorisationUtil;
import uk.gov.companieshouse.api.util.security.TokenPermissions;
import uk.gov.companieshouse.api.util.security.Permission;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class BlockOverseasCompanyInterceptor implements HandlerInterceptor {
    
    private static final String COMPANY_ACCOUNTS_READ_UPDATE_PERMISSION_ERROR =
        "Token does not have required permissions READ and UPDATE for company accounts";

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        TokenPermissions tokenPermissions = getTokenPermissions(request)
            .orElseThrow(() -> new IllegalStateException("Token permissions not found in request"));
        
        var hasCompanyAccountRead = tokenPermissions.hasPermission(Permission.Key.COMPANY_ACCOUNTS, Permission.Value.READ);
        var hasCompanyAccountWrite = tokenPermissions.hasPermission(Permission.Key.COMPANY_ACCOUNTS, Permission.Value.UPDATE);

        var hasPermissions = hasCompanyAccountRead && hasCompanyAccountWrite;


        if (!hasPermissions) {
            LOGGER.info(COMPANY_ACCOUNTS_READ_UPDATE_PERMISSION_ERROR);
            throw new PermissionException(COMPANY_ACCOUNTS_READ_UPDATE_PERMISSION_ERROR);
        }

        LOGGER.info("Token has required permissions READ and UPDATE for company accounts");
        return hasPermissions;
    }


    private Optional<TokenPermissions> getTokenPermissions(HttpServletRequest request) {
        return AuthorisationUtil.getTokenPermissions(request);
    }
    
}
