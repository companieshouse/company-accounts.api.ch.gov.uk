package uk.gov.companieshouse.api.accounts.parent;

import java.util.EnumMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.Parent;
import uk.gov.companieshouse.api.accounts.links.LinkType;

@Component
public class ParentResourceFactory<L extends LinkType> {

    private final EnumMap<Parent, ParentResource<L>> parentResourceMap = new EnumMap<>(Parent.class);

    @Autowired
    public ParentResourceFactory(List<ParentResource<L>> parentResourceList) {

        for (ParentResource<L> parentResource : parentResourceList) {

            parentResourceMap.put(parentResource.getParent(), parentResource);
        }
    }

    public ParentResource<L> getParentResource(Parent parent) {

        ParentResource<L> parentResource = parentResourceMap.get(parent);

        if (parentResource == null) {
            throw new RuntimeException("No parent resource for parent type: " + parent.toString());
        }
        return parentResource;
    }
}
