package uk.gov.companieshouse.api.accounts.request;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.Resource;

@Component
public class ResourceConverter extends PropertyEditorSupport {

    private static final Map<String, Resource> RESOURCE_MAP = new HashMap<>();

    ResourceConverter() {

        Arrays.stream(Resource.values()).forEach(resource ->
            RESOURCE_MAP.put(resource.getName(), resource)
        );
    }

    @Override
    public void setAsText(final String type) {

        Resource resource = RESOURCE_MAP.get(type);
        if (resource == null) {
            throw new RuntimeException();
        }
        setValue(resource);
    }
}