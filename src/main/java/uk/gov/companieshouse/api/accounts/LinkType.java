package uk.gov.companieshouse.api.accounts;

public enum LinkType {
    SELF("self"),
    COMPANY_ACCOUNT("company_account"),
    SMALL_FULL("small_full_accounts"),
    ACCOUNTING_POLICY_NOTE("accounting_policy_note"),
    APPROVAL("approval"),
    CREDITORS_AFTER_MORE_THAN_ONE_YEAR_NOTE("creditors_after_more_than_one_year_note"),
    CREDITORS_WITHIN_ONE_YEAR_NOTE("creditors_within_one_year_note"),
    CURRENT_PERIOD("current_period"),
    DEBTORS_NOTE("debtors_note"),
    INTANGIBLE_ASSETS_NOTE("intangible_assets_note"),
    PAYMENT("payment"),
    PREVIOUS_PERIOD("previous_period"),
    STOCKS_NOTE("stocks_note"),
    TANGIBLE_ASSETS_NOTE("tangible_assets_note");

    private String link;

    LinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

}