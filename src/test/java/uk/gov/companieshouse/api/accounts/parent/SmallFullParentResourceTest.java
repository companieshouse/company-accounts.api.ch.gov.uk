package uk.gov.companieshouse.api.accounts.parent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmallFullParentResourceTest {

    @Mock
    private SmallFullService smallFullService;

    @InjectMocks
    private ParentResource<SmallFullLinkType> smallFullParentResource = new SmallFullParentResource();

    @Mock
    private HttpServletRequest request;

    @Mock
    private SmallFull smallFull;

    @Mock
    private Map<String, String> smallFullLinks;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String PARENT_LINK = "parentLink";

    @Test
    @DisplayName("Child exists - no link")
    void childExistsNoLink() {

        when(request.getAttribute(AttributeName.SMALLFULL.getValue())).thenReturn(smallFull);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(smallFullLinks.get(SmallFullLinkType.ACCOUNTING_POLICIES_NOTE.getLink())).thenReturn(null);
        assertFalse(smallFullParentResource.childExists(request, SmallFullLinkType.ACCOUNTING_POLICIES_NOTE));
    }

    @Test
    @DisplayName("Child exists - has link")
    void childExistsHasLink() {

        when(request.getAttribute(AttributeName.SMALLFULL.getValue())).thenReturn(smallFull);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(smallFullLinks.get(SmallFullLinkType.ACCOUNTING_POLICIES_NOTE.getLink())).thenReturn(PARENT_LINK);
        assertTrue(smallFullParentResource.childExists(request, SmallFullLinkType.ACCOUNTING_POLICIES_NOTE));
    }

    @Test
    @DisplayName("Add link")
    void addLink() throws DataException {

        smallFullParentResource.addLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.ACCOUNTING_POLICIES_NOTE, PARENT_LINK, request);
        verify(smallFullService).addLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.ACCOUNTING_POLICIES_NOTE, PARENT_LINK, request);
    }

    @Test
    @DisplayName("Remove link")
    void removeLink() throws DataException {

        smallFullParentResource.removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.ACCOUNTING_POLICIES_NOTE, request);
        verify(smallFullService).removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.ACCOUNTING_POLICIES_NOTE, request);
    }

    @Test
    @DisplayName("Get parent")
    void getParent() {

        assertEquals(AccountType.SMALL_FULL, smallFullParentResource.getParent());
    }
}
