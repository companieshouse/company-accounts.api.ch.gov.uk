package uk.gov.companieshouse.api.accounts.model.ixbrl.company;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Company {

    @JsonProperty("company_number")
    private String companyNumber;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("jurisdiction")
    private String jurisdiction;
    
    public String getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(String companyNumber) {  
        getCompanyJurisdiction(companyNumber);
    }

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
    
    private void getCompanyJurisdiction(String companyNumber) {
        String companyPrefix = companyNumber.substring(0,2);
        
        switch (companyPrefix) {
        
        case "NI":
            jurisdiction = "Northern Ireland";
            break;
        case "SC":
            jurisdiction = "Scotland";
            break;
        default:
            jurisdiction = "England and Wales";
        }
    }
}
