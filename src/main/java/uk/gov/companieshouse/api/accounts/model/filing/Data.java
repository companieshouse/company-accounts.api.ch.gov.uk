package uk.gov.companieshouse.api.accounts.model.filing;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {

    @JsonProperty("links")
    private List<Link> links;

    @JsonProperty("period_end_on")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date periodEndOn;

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Date getPeriodEndOn() {
        return periodEndOn;
    }

    public void setPeriodEndOn(Date periodEndOn) {
        this.periodEndOn = periodEndOn;
    }
}
