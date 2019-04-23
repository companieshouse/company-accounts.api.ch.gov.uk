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
    STOCKS_NOTE("small-full-accounts-note#stocks"),
    TANGIBLE_ASSETS_NOTE("small-full-accounts-note#tangible-assets"),
    FIXED_ASSETS_INVESTMENTS_NOTE("small-full-accounts-note#fixed-assets-investments"),
    EMPLOYEES_NOTE("small-full-accounts-note#employees"),
    CIC_REPORT("cic-report#cic-report"),
    CIC_APPROVAL("cic-report#approval"),
    CIC_STATEMENTS("cic-report#cic-statements");

    private String value;

    Kind(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
