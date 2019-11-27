package uk.gov.companieshouse.api.accounts.model.entity.directorsreport;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

import java.util.Map;

public class DirectorsReportDataEntity extends BaseDataEntity {

    @Field("company_policy_on_disabled_employees")
    private String companyPolicyOnDisabledEmployees;

    @Field("directors")
    private Map<String, String> directorsEntity;

    @Field("political_and_charitable_donations")
    private String politicalAndCharitableDonations;

    @Field("principal_activities")
    private String principalActivities;

    @Field("secretaries")
    private Map<String, String> secretariesEntity;

    @Field("additional_information")
    private String additionalInformation;

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

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

    @Override public String toString() {
        return "DirectorsReportDataEntity{" +
                "companyPolicyOnDisabledEmployers=" + companyPolicyOnDisabledEmployees +
                ", directors=" + directorsEntity +
                ", politicalAndCharitableDonations=" + politicalAndCharitableDonations +
                ", principalActivities=" + principalActivities +
                ", secretaries=" + secretariesEntity +
                ", additionalInformation" + additionalInformation +
                "}";
    }

}
