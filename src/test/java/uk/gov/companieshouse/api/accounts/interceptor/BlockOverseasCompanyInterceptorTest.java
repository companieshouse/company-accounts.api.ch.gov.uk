package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.gov.companieshouse.api.accounts.exception.PermissionException;
import uk.gov.companieshouse.api.util.security.AuthorisationUtil;
import uk.gov.companieshouse.api.util.security.TokenPermissions;
import uk.gov.companieshouse.api.util.security.Permission;

@ExtendWith(MockitoExtension.class)
class BlockOverseasCompanyInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private BlockOverseasCompanyInterceptor interceptor;

    @Test
    @DisplayName("preHandle returns true when token has both READ and UPDATE permissions")
    void preHandleReturnsTrueWhenReadAndUpdatePermissionsArePresent() {
        TokenPermissions tokenPermissions = mock(TokenPermissions.class);
        when(tokenPermissions.hasPermission(Permission.Key.COMPANY_ACCOUNTS, Permission.Value.READ)).thenReturn(true);
        when(tokenPermissions.hasPermission(Permission.Key.COMPANY_ACCOUNTS, Permission.Value.UPDATE)).thenReturn(true);

        try (MockedStatic<AuthorisationUtil> authorisationUtil = Mockito.mockStatic(AuthorisationUtil.class)) {
            authorisationUtil.when(() -> AuthorisationUtil.getTokenPermissions(request))
                .thenReturn(Optional.of(tokenPermissions));

            boolean result = interceptor.preHandle(request, response, new Object());

            assertTrue(result);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "false, true",
        "true, false",
        "false, false"
    })
    @DisplayName("preHandle throws PermissionException when either READ or UPDATE permission is missing")
    void preHandleThrowsPermissionExceptionWhenReadPermissionIsMissing(boolean readPermission, boolean updatePermission) {
        TokenPermissions tokenPermissions = mock(TokenPermissions.class);
        when(tokenPermissions.hasPermission(Permission.Key.COMPANY_ACCOUNTS, Permission.Value.READ)).thenReturn(readPermission);
        when(tokenPermissions.hasPermission(Permission.Key.COMPANY_ACCOUNTS, Permission.Value.UPDATE)).thenReturn(updatePermission);

        try (MockedStatic<AuthorisationUtil> authorisationUtil = Mockito.mockStatic(AuthorisationUtil.class)) {
            authorisationUtil.when(() -> AuthorisationUtil.getTokenPermissions(request))
                .thenReturn(Optional.of(tokenPermissions));

            assertThrowsExactly(PermissionException.class, () -> interceptor.preHandle(request, response, new Object()));
        }
    }

    @Test
    @DisplayName("preHandle throws IllegalStateException when token permissions are missing")
    void preHandleThrowsIllegalStateExceptionWhenTokenPermissionsAreMissing() {
        try (MockedStatic<AuthorisationUtil> authorisationUtil = Mockito.mockStatic(AuthorisationUtil.class)) {
            authorisationUtil.when(() -> AuthorisationUtil.getTokenPermissions(request))
                .thenReturn(Optional.empty());

            assertThrowsExactly(IllegalStateException.class, () -> interceptor.preHandle(request, response, new Object()));
        }
    }
}
