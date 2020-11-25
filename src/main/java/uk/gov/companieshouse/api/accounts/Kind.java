package uk.gov.companieshouse.api.accounts;

public enum Kind {

    COMPANY_ACCOUNTS("company-accounts#company-accounts"),
    SMALL_FULL_ACCOUNT("small-full-accounts#small-full-accounts"),
    APPROVAL("small-full-accounts#approval"),
    CURRENT_PERIOD("small-full-accounts#current-period"),
    PREVIOUS_PERIOD("small-full-accounts#previous-period"),
    POLICY_NOTE("small-full-accounts-note#accounting-policy"),
    CREDITORS_AFTER_ONE_YEAR_NOTE("small-full-accounts-note#creditors-after-one-year"),
    CREDITORS_DUE_WITHIN_ONE_YEAR_NOTE("small-full-accounts-note#creditors-within-one-year"),
    DEBTORS_NOTE("small-full-accounts-note#debtors"),
    INTANGIBLE_ASSETS_NOTE("small-full-accounts-note#intangible-assets"),
    SMALL_FULL_STATEMENT("small-full-accounts#statements"),
    STOCKS_NOTE("small-full-accounts#stocks-note"),
    TANGIBLE_ASSETS_NOTE("small-full-accounts-note#tangible-assets"),
    FIXED_ASSETS_INVESTMENTS_NOTE("small-full-accounts-note#fixed-assets-investments"),
    CURRENT_ASSETS_INVESTMENT_NOTE("small-full-accounts-note#current-assets-investments"),
    EMPLOYEES_NOTE("small-full-accounts-note#employees"),
    CIC_REPORT("cic-report#cic-report"),
    CIC_APPROVAL("cic-report#approval"),
    CIC_STATEMENTS("cic-report#cic-statements"),
    PROFIT_LOSS_CURRENT("small-full-accounts-profit-and-loss#current"),
    PROFIT_LOSS_PREVIOUS("small-full-accounts-profit-and-loss#previous"),
    DIRECTORS_REPORT("small-full-accounts#directors-report"),
    DIRECTORS_REPORT_DIRECTOR("small-full-accounts-directors-report#directors"),
    DIRECTORS_REPORT_STATEMENTS("small-full-accounts-directors-report#statements"),
    DIRECTORS_REPORT_SECRETARY("small-full-accounts-directors-report#secretary"),
    DIRECTORS_REPORT_APPROVAL("small-full-accounts-directors-report#approval"),
    LOANS_TO_DIRECTORS("small-full-accounts-note#loans-to-directors"),
    LOANS_TO_DIRECTORS_LOANS("small-full-accounts-loans-to-directors#loans"),
    LOANS_TO_DIRECTORS_ADDITIONAL_INFO("small-full-accounts-loans-to-directors#additional-information"),
    RELATED_PARTY_TRANSACTIONS("small-full-accounts-note#related-party-transactions"),
    RPT_TRANSACTIONS("small-full-accounts-note-related-party-transactions#transactions"),
    RELATED_PARTY_TRANSACTIONS_ADDITIONAL_INFO("small-full-accounts-related-party-transactions#additional-information");

    private String value;

    Kind(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
