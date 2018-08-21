package uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BalanceSheetStatements {

    @JsonProperty("section477")
    private String section477;
    @JsonProperty("audit_not_required_by_members")
    private String auditNotRequiredByMembers;
    @JsonProperty("directors_responsibility")
    private String directorsResponsibility;
    @JsonProperty("small_companies_regime")
    private String smallCompaniesRegime;

    public String getSection477() {
        return section477;
    }

    public void setSection477(String section477) {
        this.section477 = section477;
    }

    public String getAuditNotRequiredByMembers() {
        return auditNotRequiredByMembers;
    }

    public void setAuditNotRequiredByMembers(String auditNotRequiredByMembers) {
        this.auditNotRequiredByMembers = auditNotRequiredByMembers;
    }

    public String getDirectorsResponsibility() {
        return directorsResponsibility;
    }

    public void setDirectorsResponsibility(String directorsResponsibility) {
        this.directorsResponsibility = directorsResponsibility;
    }

    public String getSmallCompaniesRegime() {
        return smallCompaniesRegime;
    }

    public void setSmallCompaniesRegime(String smallCompaniesRegime) {
        this.smallCompaniesRegime = smallCompaniesRegime;
    }
}
