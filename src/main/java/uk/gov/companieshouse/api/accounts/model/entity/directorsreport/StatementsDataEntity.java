package uk.gov.companieshouse.api.accounts.model.entity.directorsreport;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class StatementsDataEntity extends BaseDataEntity {

    @Field("additional_information")
    private String additionalInformation;

    @Field("company_policy_on_disabled_employees")
    private String companyPolicyOnDisabledEmployees;

    @Field("political_and_charitable_donations")
    private String politicalAndCharitableDonations;

    @Field("principal_activities")
    private String principalActivities;

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

    @Override
    public String toString() {
        return "StatementsDataEntity{" +
                "additionalInformation" + additionalInformation +
                ", companyPolicyOnDisabledEmployees" + companyPolicyOnDisabledEmployees +
                ", politicalAndCharitableDonations" + politicalAndCharitableDonations +
                ", principalActivities" + principalActivities +
                "}";
    }
}
