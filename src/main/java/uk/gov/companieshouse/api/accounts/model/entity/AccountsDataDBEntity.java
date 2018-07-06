package uk.gov.companieshouse.api.accounts.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.LinkType;

public class AccountsDataDBEntity {

    @Field("id")
    @JsonProperty("id")
    private String id;

    @NotNull
    @Field("period_end_on")
    @JsonProperty("period_end_on")
    private LocalDate periodEndOn;

    @JsonMerge()
    private Map<String, String> links = new HashMap<>();

    private String kind;

    private String etag;

    protected AccountsDataDBEntity build(String id){
        this.id = id;
        this.etag = GenerateEtagUtil.generateEtag();
        this.kind = Kind.ACCOUNT.getValue();

        Map<String, String> links = new HashMap<>();
        links.put(LinkType.SELF.getLink(), "links/accounts");
        this.links = links;
        return this;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getPeriodEndOn() {
        return periodEndOn;
    }

    public void setPeriodEndOn(LocalDate periodEndOn) {
        this.periodEndOn = periodEndOn;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

}