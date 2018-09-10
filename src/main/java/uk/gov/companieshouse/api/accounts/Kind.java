package uk.gov.companieshouse.api.accounts;

public enum Kind {

    COMPANY_ACCOUNTS("company-accounts#company-accounts"),
    SMALL_FULL_ACCOUNT("small-full-accounts#small-full-accounts"),
    APPROVAL("small-full-accounts#approval"),
    CURRENT_PERIOD("small-full-accounts#current-period"),
    PREVIOUS_PERIOD("small-full-accounts#previous-period"),
    POLICY_NOTE("small-full-accounts#policy-note"),
    CREDITORS_AFTER_ONE_YEAR_NOTE("small-full-accounts#creditors-after-one-year-note"),
    CREDITORS_DUE_WITHIN_ONE_YEAR_NOTE("small-full-accounts#creditors-due-within-one-year-note"),
    DEBTORS_NOTE("small-full-accounts#debtors-note"),
    INTANGIBLE_ASSETS_NOTE("small-full-accounts#intangible-assets-note"),
    STOCKS_NOTE("small-full-accounts#stocks-note"),
    TANGIBLE_ASSETS_NOTE("small-full-accounts#tangible-assets-note");

    private String value;

    Kind(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}