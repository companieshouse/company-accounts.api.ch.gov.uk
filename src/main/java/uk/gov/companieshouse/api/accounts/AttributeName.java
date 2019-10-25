package uk.gov.companieshouse.api.accounts;

public enum AttributeName {

    TRANSACTION("transaction"),
    COMPANY_ACCOUNT("accounts"),
    SMALLFULL("small-full"),
    CURRENT_PERIOD("current-period"),
    PREVIOUS_PERIOD("previous-period"),
    CIC_REPORT("cic-report");

    private String value;

    AttributeName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
