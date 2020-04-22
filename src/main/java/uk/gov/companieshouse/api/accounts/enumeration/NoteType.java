package uk.gov.companieshouse.api.accounts.enumeration;

public enum NoteType {

    ACCOUNTING_POLICIES("accounting-policies"),
    EMPLOYEES("employees"),
    INTANGIBLE_ASSETS("intangible-assets"),
    TANGIBLE_ASSETS("tangible-assets"),
    FIXED_ASSETS_INVESTMENTS("fixed-assets-investments"),
    STOCKS("stocks"),
    DEBTORS("debtors"),
    CURRENT_ASSETS_INVESTMENTS("current-assets-investments"),
    CREDITORS_AFTER("creditors-after-more-than-one-year"),
    CREDITORS_WITHIN("creditors-within-one-year"),
    OFF_BALANCE_SHEET_ARRANGEMENTS("off-balance-sheet-arrangements");

    NoteType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }
}
