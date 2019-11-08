package uk.gov.companieshouse.api.accounts.model;

import java.util.List;

public class AccountTypeConfig {

    private List<String> soloResources;

    private List<String> notes;

    private List<String> currentPeriod;

    public List<String> getSoloResources() {
        return soloResources;
    }

    public void setSoloResources(List<String> soloResources) {
        this.soloResources = soloResources;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public List<String> getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(List<String> currentPeriod) {
        this.currentPeriod = currentPeriod;
    }
}
