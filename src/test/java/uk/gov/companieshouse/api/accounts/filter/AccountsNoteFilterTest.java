package uk.gov.companieshouse.api.accounts.filter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import uk.gov.companieshouse.api.accounts.utility.PackageResolver;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountsNoteFilterTest {

    @Mock
    private ServletResponse servletResponse;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PackageResolver packageResolver;

    @InjectMocks
    private AccountsNoteFilter filter;

    private static final String ACCOUNT_TYPE = "account-type";

    private static final String NOTE_TYPE = "note-type";

    private static final String NOTE_URI = "/transactions/transactionId/company-accounts/companyAccountId/" + ACCOUNT_TYPE + "/notes/" + NOTE_TYPE;

    private static final String OTHER_URI = "/transactions/transactionId/company-accounts/companyAccountId/" + ACCOUNT_TYPE + "/other-resource";

    @Test
    @DisplayName("Submit note")
    void submitNote() throws IOException, ServletException {

        MockHttpServletRequest servletRequest = new MockHttpServletRequest("POST", NOTE_URI);
        servletRequest.setContent("{}".getBytes());
        filter.doFilter(servletRequest, servletResponse, filterChain);

        verify(packageResolver).getNotePackage(ACCOUNT_TYPE, NOTE_TYPE);
    }

    @Test
    @DisplayName("Get note")
    void getNote() throws IOException, ServletException {

        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", NOTE_URI);
        filter.doFilter(servletRequest, servletResponse, filterChain);

        verify(packageResolver, never()).getNotePackage(ACCOUNT_TYPE, NOTE_TYPE);
    }

    @Test
    @DisplayName("Submit other resource")
    void submitOtherResource() throws IOException, ServletException {

        MockHttpServletRequest servletRequest = new MockHttpServletRequest("POST", OTHER_URI);
        servletRequest.setContent("{}".getBytes());
        filter.doFilter(servletRequest, servletResponse, filterChain);

        verify(packageResolver, never()).getNotePackage(eq(ACCOUNT_TYPE), anyString());
    }
}
