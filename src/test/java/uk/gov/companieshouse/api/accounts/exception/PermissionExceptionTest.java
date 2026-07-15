package uk.gov.companieshouse.api.accounts.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PermissionExceptionTest {

    @Test
    @DisplayName("Constructor sets exception message")
    void constructorSetsMessage() {
        PermissionException permissionException = new PermissionException("permissions required");

        assertEquals("permissions required", permissionException.getMessage());
    }

    @Test
    @DisplayName("Constructor sets message and cause")
    void constructorSetsMessageAndCause() {
        RuntimeException cause = new RuntimeException("root cause");
        PermissionException permissionException = new PermissionException("permissions required", cause);

        assertEquals("permissions required", permissionException.getMessage());
        assertSame(cause, permissionException.getCause());
    }
}
