package uk.gov.companieshouse.api.accounts.request;

import java.beans.PropertyEditorSupport;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.Resource;

@Component
public class ResourceConverter extends PropertyEditorSupport {

    @Override
    public void setAsText(final String type) {
        setValue(Resource.fromString(type));
    }
}