package uk.gov.companieshouse.api.accounts.model.ixbrl.period;

public class Period {

    private String currentPeriodStartOn;
    private String currentPeriodEndsOn;
    private String previousPeriodStartOn;
    private String previousPeriodEndsOn;

    public String getCurrentPeriodStartOn() {
        return currentPeriodStartOn;
    }

    public void setCurrentPeriodStartOn(String currentPeriodStartOn) {
        this.currentPeriodStartOn = currentPeriodStartOn;
    }

    public String getCurrentPeriodEndsOn() {
        return currentPeriodEndsOn;
    }

    public void setCurrentPeriodEndsOn(String currentPeriodEndsOn) {
        this.currentPeriodEndsOn = currentPeriodEndsOn;
    }

    public String getPreviousPeriodStartOn() {
        return previousPeriodStartOn;
    }

    public void setPreviousPeriodStartOn(String previousPeriodStartOn) {
        this.previousPeriodStartOn = previousPeriodStartOn;
    }

    public String getPreviousPeriodEndsOn() {
        return previousPeriodEndsOn;
    }

    public void setPreviousPeriodEndsOn(String previousPeriodEndsOn) {
        this.previousPeriodEndsOn = previousPeriodEndsOn;
    }
}
