package uk.gov.companieshouse.api.accounts.model.ixbrl;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.ixbrl.company.Company;
import uk.gov.companieshouse.api.accounts.model.ixbrl.notes.Notes;
import uk.gov.companieshouse.api.accounts.model.ixbrl.period.Period;

public class Account {

    @JsonProperty("period")
    private Period period;
    @JsonProperty("balance_sheet")
    private BalanceSheet balanceSheet;
    @JsonProperty("notes")
    private Notes notes;
    @JsonProperty("company")
    private Company company;

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public BalanceSheet getBalanceSheet() {
        return balanceSheet;
    }

    public void setBalanceSheet(BalanceSheet balanceSheet) {
        this.balanceSheet = balanceSheet;
    }

    public Notes getNotes() {
        return notes;
    }

    public void setNotes(Notes notes) {
        this.notes = notes;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
