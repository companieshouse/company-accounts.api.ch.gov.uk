package uk.gov.companieshouse.api.accounts.model.ixbrl.company;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Company {

    @JsonProperty("company_number")
    private String companyNumber;
    @JsonProperty("company_name")
    private String companyName;

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
