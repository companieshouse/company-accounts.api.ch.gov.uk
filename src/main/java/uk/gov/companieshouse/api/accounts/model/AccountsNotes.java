package uk.gov.companieshouse.api.accounts.model;

import java.util.List;
import java.util.Map;

public class AccountsNotes {

    private Map<String, List<String>> notes;

    public Map<String, List<String>> getNotes() {
        return notes;
    }

    public void setNotes(Map<String, List<String>> notes) {
        this.notes = notes;
    }
}
