package uk.gov.companieshouse.api.accounts.model.ixbrl.notes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notes {

    @JsonProperty("post_balance_sheet_events")
    private PostBalanceSheetEvents postBalanceSheetEvents;

    public PostBalanceSheetEvents getPostBalanceSheetEvents() {
        return postBalanceSheetEvents;
    }

    public void setPostBalanceSheetEvents(PostBalanceSheetEvents postBalanceSheetEvents) {
        this.postBalanceSheetEvents = postBalanceSheetEvents;
    }
}
