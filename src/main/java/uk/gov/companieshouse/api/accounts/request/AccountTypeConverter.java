package uk.gov.companieshouse.api.accounts.request;

import java.beans.PropertyEditorSupport;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;

@Component
public class AccountTypeConverter extends PropertyEditorSupport {

    @Override
    public void setAsText(final String type) {
        setValue(AccountType.fromString(type));
    }
}