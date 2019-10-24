package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Component
public class PeriodConverter extends PropertyEditorSupport {

    @Override
    public void setAsText(final String type) {
        setValue(Period.fromString(type));
    }
}