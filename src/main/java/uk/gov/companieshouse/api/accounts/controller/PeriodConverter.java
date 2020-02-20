package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingPeriod;

import java.beans.PropertyEditorSupport;

@Component
public class PeriodConverter extends PropertyEditorSupport {

    @Override
    public void setAsText(final String type) {
        setValue(AccountingPeriod.fromString(type));
    }
}