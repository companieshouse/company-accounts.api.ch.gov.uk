package uk.gov.companieshouse.api.accounts.model.ixbrl.notes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostBalanceSheetEvents {

    @JsonProperty("current_period_date_formatted")
    private String currentPeriodDateFormatted;
    @JsonProperty("post_balance_sheet_events_info")
    private String postBalanceSheetEventsInfo;

    public String getCurrentPeriodDateFormatted() {
        return currentPeriodDateFormatted;
    }

    public void setCurrentPeriodDateFormatted(String currentPeriodDateFormatted) {
        this.currentPeriodDateFormatted = currentPeriodDateFormatted;
    }

    public String getPostBalanceSheetEventsInfo() {
        return postBalanceSheetEventsInfo;
    }

    public void setPostBalanceSheetEventsInfo(String postBalanceSheetEventsInfo) {
        this.postBalanceSheetEventsInfo = postBalanceSheetEventsInfo;
    }
}


