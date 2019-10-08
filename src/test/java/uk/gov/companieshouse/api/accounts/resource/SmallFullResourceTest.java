package uk.gov.companieshouse.api.accounts.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmallFullResourceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private LinkType linkType;

    @Mock
    private SmallFull smallFull;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private SmallFullResource smallFullResource;

    @Test
    @DisplayName("Link type not present")
    void linkTypeNotPresent() {

        when(request.getAttribute(AttributeName.SMALLFULL.getValue())).thenReturn(smallFull);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(linkType.getLink())).thenReturn(null);

        assertFalse(smallFullResource.hasLink(request, linkType));
    }

    @Test
    @DisplayName("Link type present")
    void linkTypePresent() {

        when(request.getAttribute(AttributeName.SMALLFULL.getValue())).thenReturn(smallFull);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(linkType.getLink())).thenReturn("");

        assertTrue(smallFullResource.hasLink(request, linkType));
    }
}
