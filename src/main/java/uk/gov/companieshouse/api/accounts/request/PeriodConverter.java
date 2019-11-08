package uk.gov.companieshouse.api.accounts.request;

import java.beans.PropertyEditorSupport;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.Period;

@Component
public class PeriodConverter extends PropertyEditorSupport {

    @Override
    public void setAsText(final String type) {
        setValue(Period.fromString(type));
    }
}