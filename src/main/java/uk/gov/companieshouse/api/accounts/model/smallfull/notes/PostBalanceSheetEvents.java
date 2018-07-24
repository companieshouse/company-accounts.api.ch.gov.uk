package uk.gov.companieshouse.api.accounts.model.smallfull.notes;

public class PostBalanceSheetEvents {

    private String currentPeriodDateFormatted;
    private String previousPeriodDateFormatted;
    private String postBalanceSheetEventsInfo;

    public String getCurrentPeriodDateFormatted() {
        return currentPeriodDateFormatted;
    }

    public void setCurrentPeriodDateFormatted(String currentPeriodDateFormatted) {
        this.currentPeriodDateFormatted = currentPeriodDateFormatted;
    }

    public String getPreviousPeriodDateFormatted() {
        return previousPeriodDateFormatted;
    }

    public void setPreviousPeriodDateFormatted(String previousPeriodDateFormatted) {
        this.previousPeriodDateFormatted = previousPeriodDateFormatted;
    }

    public String getPostBalanceSheetEventsInfo() {
        return postBalanceSheetEventsInfo;
    }

    public void setPostBalanceSheetEventsInfo(String postBalanceSheetEventsInfo) {
        this.postBalanceSheetEventsInfo = postBalanceSheetEventsInfo;
    }
}


