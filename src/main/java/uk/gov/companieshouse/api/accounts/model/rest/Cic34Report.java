package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;
import com.google.gson.Gson;

@JsonInclude(Include.NON_NULL)
public class Cic34Report extends RestObject {

    private static final int MAX_FIELD_LENGTH = 20000;

    @NotBlank
    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("company_activities_and_impact")
    private String companyActivitiesAndImpact;

    @NotBlank
    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("consultation_with_stakeholders")
    private String consultationWithStakeholders;

    @NotBlank
    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("directors_remuneration")
    private String directorsRemuneration;

    @NotBlank
    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("transfer_of_assets")
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

    @Override
    public String toString() { return new Gson().toJson(this); }
}
