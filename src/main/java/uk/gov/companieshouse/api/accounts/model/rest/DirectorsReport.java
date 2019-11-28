package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirectorsReport extends RestObject {

    @JsonProperty("company_policy_on_disabled_employees")
    private String companyPolicyOnDisabledEmployees;

    @JsonProperty("directors")
    private Map<String, String> directors;

    @JsonProperty("political_and_charitable_donations")
    private String politicalAndCharitableDonations;

    @JsonProperty("principal_activities")
    private String principalActivities;

    @JsonProperty("secretaries")
    private Map<String, String> secretaries;

    @JsonProperty("additional_information")
    private String additionalInformation;

    public String getCompanyPolicyOnDisabledEmployees() {
        return companyPolicyOnDisabledEmployees;
    }

    public void setCompanyPolicyOnDisabledEmployees(String companyPolicyOnDisabledEmployees) {
        this.companyPolicyOnDisabledEmployees = companyPolicyOnDisabledEmployees;
    }

    public Map<String, String> getDirectors() {
        return directors;
    }

    public void setDirectors(Map<String, String> directors) {
        this.directors = directors;
    }

    public String getPoliticalAndCharitableDonations() {
        return politicalAndCharitableDonations;
    }

    public void setPoliticalAndCharitableDonations(String politicalAndCharitableDonations) {
        this.politicalAndCharitableDonations = politicalAndCharitableDonations;
    }

    public String getPrincipalActivities() {
        return principalActivities;
    }

    public void setPrincipalActivities(String principalActivities) {
        this.principalActivities = principalActivities;
    }

    public Map<String, String> getSecretaries() {
        return secretaries;
    }

    public void setSecretaries(Map<String, String> secretaries) {
        this.secretaries = secretaries;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
