package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirectorsReport extends RestObject {

    @JsonProperty("company_policy_on_disabled_employees")
    private String companyPolicyOnDisabledEmployees;

    @JsonProperty("directors")
    private Map<String, String> directorsEntity;

    @JsonProperty("political_and_charitable_donations")
    private String politicalAndCharitableDonations;

    @JsonProperty("principal_activities")
    private String principalActivities;

    @JsonProperty("secretaries")
    private Map<String, String> secretariesEntity;

    @JsonProperty("additional_information")
    private String additionalInformation;

    public String getCompanyPolicyOnDisabledEmployees() {
        return companyPolicyOnDisabledEmployees;
    }

    public void setCompanyPolicyOnDisabledEmployees(String companyPolicyOnDisabledEmployees) {
        this.companyPolicyOnDisabledEmployees = companyPolicyOnDisabledEmployees;
    }

    public Map<String, String> getDirectorsEntity() {
        return directorsEntity;
    }

    public void setDirectorsEntity(Map<String, String> directorsEntity) {
        this.directorsEntity = directorsEntity;
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

    public Map<String, String> getSecretariesEntity() {
        return secretariesEntity;
    }

    public void setSecretariesEntity(Map<String, String> secretariesEntity) {
        this.secretariesEntity = secretariesEntity;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
