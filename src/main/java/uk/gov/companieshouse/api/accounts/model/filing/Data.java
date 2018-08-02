package uk.gov.companieshouse.api.accounts.model.filing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public class Data {

    @JsonProperty("links")
    private List<Link> links;

    @JsonProperty("period_end_on")
    private LocalDate periodEndOn;

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public LocalDate getPeriodEndOn() {
        return periodEndOn;
    }

    public void setPeriodEndOn(LocalDate periodEndOn) {
        this.periodEndOn = periodEndOn;
    }
}
