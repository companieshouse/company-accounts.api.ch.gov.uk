package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.gov.companieshouse.api.util.security.AuthorisationUtil;
import uk.gov.companieshouse.api.util.security.Permission.Key;
import uk.gov.companieshouse.api.util.security.Permission.Value;
import uk.gov.companieshouse.api.util.security.SecurityConstants;
import uk.gov.companieshouse.api.util.security.TokenPermissions;

@ExtendWith(MockitoExtension.class)
class AuthenticationInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationInterceptor interceptor;

    @Test
    @DisplayName("preHandle returns true when identity type is API key")
    void preHandleReturnsTrueForApiKeyRequest() {
        try (MockedStatic<AuthorisationUtil> authorisationUtil = Mockito.mockStatic(AuthorisationUtil.class)) {
            authorisationUtil.when(() -> AuthorisationUtil.getAuthorisedIdentityType(request))
                .thenReturn(SecurityConstants.API_KEY_IDENTITY_TYPE);

            boolean result = interceptor.preHandle(request, response, new Object());

            assertTrue(result);
        }
    }

    @Test
    @DisplayName("preHandle returns true when token has COMPANY_ACCOUNTS=UPDATE permission")
    void preHandleReturnsTrueWhenUpdatePermissionIsPresent() {
        TokenPermissions tokenPermissions = mock(TokenPermissions.class);
        when(tokenPermissions.hasPermission(Key.COMPANY_ACCOUNTS, Value.UPDATE)).thenReturn(true);

        try (MockedStatic<AuthorisationUtil> authorisationUtil = Mockito.mockStatic(AuthorisationUtil.class)) {
            authorisationUtil.when(() -> AuthorisationUtil.getAuthorisedIdentityType(request))
                .thenReturn(SecurityConstants.TOKEN_PERMISSION_REQUEST_KEY);
            authorisationUtil.when(() -> AuthorisationUtil.getTokenPermissions(request))
                .thenReturn(Optional.of(tokenPermissions));

            boolean result = interceptor.preHandle(request, response, new Object());

            assertTrue(result);
        }
    }

    @Test
    @DisplayName("preHandle returns false and sets 403 status when COMPANY_ACCOUNTS=UPDATE permission is absent")
    void preHandleReturnsFalseWhenUpdatePermissionIsAbsent() {
        TokenPermissions tokenPermissions = mock(TokenPermissions.class);
        when(tokenPermissions.hasPermission(Key.COMPANY_ACCOUNTS, Value.UPDATE)).thenReturn(false);

        try (MockedStatic<AuthorisationUtil> authorisationUtil = Mockito.mockStatic(AuthorisationUtil.class)) {
            authorisationUtil.when(() -> AuthorisationUtil.getAuthorisedIdentityType(request))
                .thenReturn(SecurityConstants.TOKEN_PERMISSION_REQUEST_KEY);
            authorisationUtil.when(() -> AuthorisationUtil.getTokenPermissions(request))
                .thenReturn(Optional.of(tokenPermissions));

            boolean result = interceptor.preHandle(request, response, new Object());

            assertFalse(result);
            verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Test
    @DisplayName("preHandle throws IllegalStateException when token permissions are missing")
    void preHandleThrowsIllegalStateExceptionWhenTokenPermissionsAreMissing() {
        try (MockedStatic<AuthorisationUtil> authorisationUtil = Mockito.mockStatic(AuthorisationUtil.class)) {
            authorisationUtil.when(() -> AuthorisationUtil.getAuthorisedIdentityType(request))
                .thenReturn(SecurityConstants.TOKEN_PERMISSION_REQUEST_KEY);
            authorisationUtil.when(() -> AuthorisationUtil.getTokenPermissions(request))
                .thenReturn(Optional.empty());

            assertThrowsExactly(IllegalStateException.class, () -> interceptor.preHandle(request, response, new Object()));
        }
    }
}
