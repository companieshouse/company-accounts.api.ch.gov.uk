package uk.gov.companieshouse.api.accounts.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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


    /**
     * Get the self link
     *
     * @return A {@link String} or null
     */
    @JsonIgnore
    public String getSelfLink() {
        return links == null ? null
                : links.get(LinkType.SELF.getLink());
    }

    /**
     * Determine whether the given key is contained
     *
     * @param key
     * @return True or false
     */
    public boolean containsLinkKey(String key) {
        return links.containsKey(key);
    }

    /**
     * Determine whether any of the given link keys are contained
     *
     * @param linkKeys
     * @return True or false
     */
    public boolean containsAnyLinkKey(Set<String> linkKeys) {
        for (String linkKey : linkKeys) {
            if (links.containsKey(linkKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a link using the given key
     *
     * @param key
     * @param link
     * @throws IllegalArgumentException if either key or link are null or empty
     */
    public void addLink(String key, String link) {
        if (links.containsKey(key)) {
            throw new IllegalArgumentException("A link with that key is already contained");
        }
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("key cannot be empty");
        }
        if (link == null) {
            throw new IllegalArgumentException("link cannot be null");
        }
        if (link.trim().isEmpty()) {
            throw new IllegalArgumentException("link cannot be empty");
        }

        links.put(key, link);
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