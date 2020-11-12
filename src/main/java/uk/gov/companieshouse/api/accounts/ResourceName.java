package uk.gov.companieshouse.api.accounts;

public enum ResourceName {

    COMPANY_ACCOUNT("company-accounts"),
    SMALL_FULL("small-full"),
    CURRENT_PERIOD("current-period"),
    PREVIOUS_PERIOD("previous-period"),
    APPROVAL("approval"),
    ACCOUNTING_POLICIES("accounting-policy"),
    DEBTORS("debtors"),
    STATEMENTS("statements"),
    CREDITORS_WITHIN_ONE_YEAR("creditors-within-one-year"),
    CREDITORS_AFTER_ONE_YEAR("creditors-after-more-than-one-year"),
    STOCKS("stocks"),
    TANGIBLE_ASSETS("tangible-assets"),
    INTANGIBLE_ASSETS("intangible-assets"),
    CURRENT_ASSETS_INVESTMENTS("current-assets-investments"),
    FIXED_ASSETS_INVESTMENTS("fixed-assets-investments"),
    EMPLOYEES("employees"),
    CIC_REPORT("cic-report"),
    CIC_APPROVAL("cic-approval"),
    CIC_STATEMENTS("cic-statements"),
    PROFIT_LOSS("profit-and-loss"),
    DIRECTORS_REPORT("directors-report"),
    DIRECTORS("directors"),
    SECRETARY("secretary"),
    LOANS_TO_DIRECTORS("loans-to-directors"),
    LOANS("loans"),
    RELATED_PARTY_TRANSACTIONS("related-party-transactions"),
    ADDITIONAL_INFO("additional-information");

    private String name;

    ResourceName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
