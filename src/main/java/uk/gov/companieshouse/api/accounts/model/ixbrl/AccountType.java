package uk.gov.companieshouse.api.accounts.model.ixbrl;

import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.ixbrl.notes.Notes;
import uk.gov.companieshouse.api.accounts.model.ixbrl.period.Period;

public class AccountType {

    private Period period;
    private BalanceSheet balanceSheet;
    private Notes notes;

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
}
