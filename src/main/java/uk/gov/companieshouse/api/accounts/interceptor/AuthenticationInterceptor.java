package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.util.security.AuthorisationUtil;
import uk.gov.companieshouse.api.util.security.Permission.Key;
import uk.gov.companieshouse.api.util.security.Permission.Value;
import uk.gov.companieshouse.api.util.security.SecurityConstants;
import uk.gov.companieshouse.api.util.security.TokenPermissions;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (SecurityConstants.API_KEY_IDENTITY_TYPE.equals(AuthorisationUtil.getAuthorisedIdentityType(request))) {
            LOGGER.debugRequest(request, "AuthenticationInterceptor skipping token permission checks for api key request", new HashMap<>());
            return true;
        }

        // TokenPermissions should have been set up in the request by TokenPermissionsInterceptor
        final TokenPermissions tokenPermissions = getTokenPermissions(request)
                .orElseThrow(() -> new IllegalStateException("TokenPermissions object not present in request"));

        boolean hasCompanyAccountsUpdatePermission = tokenPermissions.hasPermission(Key.COMPANY_ACCOUNTS, Value.UPDATE);

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("request_method", request.getMethod());
        debugMap.put("has_company_accounts_update_permission", hasCompanyAccountsUpdatePermission);

        if (hasCompanyAccountsUpdatePermission) {
            LOGGER.debugRequest(request, "AuthenticationInterceptor authorised with company_accounts=update permission",
                    debugMap);
            return true;
        }

        LOGGER.debugRequest(request, "AuthenticationInterceptor unauthorised", debugMap);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Implement the postHandle logic here if needed.
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Implement the afterCompletion logic here if needed.
    }

    protected Optional<TokenPermissions> getTokenPermissions(HttpServletRequest request) {
        return AuthorisationUtil.getTokenPermissions(request);
    }
}
