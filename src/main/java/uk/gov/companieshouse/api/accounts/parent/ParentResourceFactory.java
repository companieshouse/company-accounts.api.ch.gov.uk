package uk.gov.companieshouse.api.accounts.parent;

import java.util.EnumMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.exception.MissingInfrastructureException;
import uk.gov.companieshouse.api.accounts.links.LinkType;

@Component
public class ParentResourceFactory<L extends LinkType> {

    private final EnumMap<AccountType, ParentResource<L>> parentResourceMap = new EnumMap<>(AccountType.class);

    @Autowired
    public ParentResourceFactory(List<ParentResource<L>> parentResourceList) {
        for (ParentResource<L> parentResource : parentResourceList) {
            parentResourceMap.put(parentResource.getParent(), parentResource);
        }
    }

    public ParentResource<L> getParentResource(AccountType accountType) {
        ParentResource<L> parentResource = parentResourceMap.get(accountType);

        if (parentResource == null) {
            throw new MissingInfrastructureException("No ParentResource for AccountType: " + accountType.toString());
        }
        return parentResource;
    }
}
