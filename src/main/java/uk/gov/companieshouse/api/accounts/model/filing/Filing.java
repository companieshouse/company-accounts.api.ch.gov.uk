package uk.gov.companieshouse.api.accounts.model.filing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class Filing {

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    @JsonProperty("description")
    private String description;

    @JsonProperty("description_values")
    private Map<String, String> descriptionValues;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("data")
    private Data data;

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(Map<String, String> descriptionValues) {
        this.descriptionValues = descriptionValues;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
