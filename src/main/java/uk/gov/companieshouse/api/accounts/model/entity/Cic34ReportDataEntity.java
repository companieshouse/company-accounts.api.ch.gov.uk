package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class Cic34ReportDataEntity extends BaseDataEntity {

    @Field("company_activities_and_impact")
    private String companyActivitiesAndImpact;

    @Field("consultation_with_stakeholders")
    private String consultationWithStakeholders;

    @Field("directors_remuneration")
    private String directorsRemuneration;

    @Field("transfer_of_assets")
    private String transferOfAssets;

    public String getCompanyActivitiesAndImpact() {
        return companyActivitiesAndImpact;
    }

    public void setCompanyActivitiesAndImpact(String companyActivitiesAndImpact) {
        this.companyActivitiesAndImpact = companyActivitiesAndImpact;
    }

    public String getConsultationWithStakeholders() {
        return consultationWithStakeholders;
    }

    public void setConsultationWithStakeholders(String consultationWithStakeholders) {
        this.consultationWithStakeholders = consultationWithStakeholders;
    }

    public String getDirectorsRemuneration() {
        return directorsRemuneration;
    }

    public void setDirectorsRemuneration(String directorsRemuneration) {
        this.directorsRemuneration = directorsRemuneration;
    }

    public String getTransferOfAssets() {
        return transferOfAssets;
    }

    public void setTransferOfAssets(String transferOfAssets) {
        this.transferOfAssets = transferOfAssets;
    }
}
