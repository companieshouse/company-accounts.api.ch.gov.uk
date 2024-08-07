package uk.gov.companieshouse.api.accounts.links;

public enum SmallFullLinkType implements LinkType {

    SELF("self"),
    ACCOUNTING_POLICIES_NOTE("accounting_policies_note"),
    APPROVAL("approval"),
    CREDITORS_AFTER_MORE_THAN_ONE_YEAR_NOTE("creditors_after_more_than_one_year_note"),
    CREDITORS_WITHIN_ONE_YEAR_NOTE("creditors_within_one_year_note"),
    CURRENT_PERIOD("current_period"),
    DEBTORS_NOTE("debtors_note"),
    INTANGIBLE_ASSETS_NOTE("intangible_assets_note"),
    PAYMENT("payment"),
    PREVIOUS_PERIOD("previous_period"),
    STATEMENTS("statements"),
    STOCKS_NOTE("stocks_note"),
    TANGIBLE_ASSETS_NOTE("tangible_assets_note"),
    CURRENT_ASSETS_INVESTMENTS_NOTE("current_assets_investments_note"),
    FIXED_ASSETS_INVESTMENTS_NOTE("fixed_assets_investments_note"),
    EMPLOYEES_NOTE("employees_note"),
    DIRECTORS_REPORT("directors_report"),
    OFF_BALANCE_SHEET_ARRANGEMENTS_NOTE("off_balance_sheet_arrangements_note"),
    FINANCIAL_COMMITMENTS_NOTE("financial_commitments_note"),
    LOANS_TO_DIRECTORS("loans_to_directors"),
    RELATED_PARTY_TRANSACTIONS("related_party_transactions");


    private final String link;

    SmallFullLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}
