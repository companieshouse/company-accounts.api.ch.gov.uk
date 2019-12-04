package uk.gov.companieshouse.api.accounts.model.rest.directorsreport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirectorsReport extends RestObject {

    @JsonProperty("directors")
    private Map<String, String> directors;

    public Map<String, String> getDirectors() {
        return directors;
    }

    public void setDirectors(Map<String, String> directors) {
        this.directors = directors;
    }
}
