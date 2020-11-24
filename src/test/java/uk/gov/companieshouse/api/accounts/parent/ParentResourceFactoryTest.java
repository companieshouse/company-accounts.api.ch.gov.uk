package uk.gov.companieshouse.api.accounts.parent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.exception.MissingInfrastructureException;
import uk.gov.companieshouse.api.accounts.links.LinkType;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParentResourceFactoryTest {

    @Mock
    private ParentResource<LinkType> parentResource;

    private AccountType accountTypeWithCorrespondingParentResource =
            AccountType.SMALL_FULL;

    private AccountType accountTypeWithoutCorrespondingParentResource =
            AccountType.MICRO;

    private ParentResourceFactory<LinkType> factory;

    @BeforeEach
    private void setup() {
        when(parentResource.getParent()).thenReturn(accountTypeWithCorrespondingParentResource);
        List<ParentResource<LinkType>> parentResources = new ArrayList<>();
        parentResources.add(parentResource);
        factory = new ParentResourceFactory<>(parentResources);
    }

    @Test
    @DisplayName("Get parent resource for account type with corresponding parent resource")
    void getParentResourceForAccountTypeWithCorrespondingParentResource() {

        assertEquals(parentResource,
                factory.getParentResource(accountTypeWithCorrespondingParentResource));
    }

    @Test
    @DisplayName("Get parent resource for account type without corresponding parent resource")
    void getParentResourceForAccountTypeWithoutCorrespondingParentResource() {

        assertThrows(MissingInfrastructureException.class,
                () -> factory.getParentResource(accountTypeWithoutCorrespondingParentResource));
    }
}
