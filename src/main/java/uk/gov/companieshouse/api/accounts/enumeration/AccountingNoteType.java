package uk.gov.companieshouse.api.accounts.enumeration;

import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;

public enum AccountingNoteType {

    SMALL_FULL_ACCOUNTING_POLICIES(
            AccountType.SMALL_FULL,
            NoteType.ACCOUNTING_POLICIES,
            Parent.SMALL_FULL,
            SmallFullLinkType.ACCOUNTING_POLICY_NOTE,
            "small-full-accounts-note#accounting-policy",
            false),
    SMALL_FULL_EMPLOYEES(
            AccountType.SMALL_FULL,
            NoteType.EMPLOYEES,
            Parent.SMALL_FULL,
            SmallFullLinkType.EMPLOYEES_NOTE,
            "small-full-accounts-note#employees",
            true),
    SMALL_FULL_INTANGIBLE_ASSETS(
            AccountType.SMALL_FULL,
            NoteType.INTANGIBLE_ASSETS,
            Parent.SMALL_FULL,
            SmallFullLinkType.INTANGIBLE_ASSETS_NOTE,
            "small-full-accounts-note#intangible-assets",
            true),
    SMALL_FULL_TANGIBLE_ASSETS(
            AccountType.SMALL_FULL,
            NoteType.TANGIBLE_ASSETS,
            Parent.SMALL_FULL,
            SmallFullLinkType.TANGIBLE_ASSETS_NOTE,
            "small-full-accounts-note#tangible-assets",
            true),
    SMALL_FULL_FIXED_ASSETS_INVESTMENTS(
            AccountType.SMALL_FULL,
            NoteType.FIXED_ASSETS_INVESTMENTS,
            Parent.SMALL_FULL,
            SmallFullLinkType.FIXED_ASSETS_INVESTMENTS_NOTE,
            "small-full-accounts-note#fixed-assets-investments",
            true),
    SMALL_FULL_STOCKS(
            AccountType.SMALL_FULL,
            NoteType.STOCKS,
            Parent.SMALL_FULL,
            SmallFullLinkType.STOCKS_NOTE,

            "small-full-accounts#stocks-note",
            true),
    SMALL_FULL_DEBTORS(
            AccountType.SMALL_FULL,
            NoteType.DEBTORS,
            Parent.SMALL_FULL,
            SmallFullLinkType.DEBTORS_NOTE,
            "small-full-accounts-note#debtors",
            true),
    SMALL_FULL_CURRENT_ASSETS_INVESTMENTS(
            AccountType.SMALL_FULL,
            NoteType.CURRENT_ASSETS_INVESTMENTS,
            Parent.SMALL_FULL,
            SmallFullLinkType.CURRENT_ASSETS_INVESTMENTS_NOTE,
            "small-full-accounts-note#current-assets-investments",
            true),
    SMALL_FULL_CREDITORS_AFTER(
            AccountType.SMALL_FULL,
            NoteType.CREDITORS_AFTER,
            Parent.SMALL_FULL,
            SmallFullLinkType.CREDITORS_AFTER_MORE_THAN_ONE_YEAR_NOTE,
            "small-full-accounts-note#creditors-after-one-year",
            true),
    SMALL_FULL_CREDITORS_WITHIN(
            AccountType.SMALL_FULL,
            NoteType.CREDITORS_WITHIN,
            Parent.SMALL_FULL,
            SmallFullLinkType.CREDITORS_WITHIN_ONE_YEAR_NOTE,
            "small-full-accounts-note#creditors-within-one-year",
            true),
    SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS(
            AccountType.SMALL_FULL,
            NoteType.OFF_BALANCE_SHEET_ARRANGEMENTS,
            Parent.SMALL_FULL,
            SmallFullLinkType.OFF_BALANCE_SHEET_ARRANGEMENTS_NOTE,
            "small-full-accounts-note#off-balance-sheet-arrangements",
            false);

    private AccountType accountType;
    private NoteType noteType;
    private Parent parent;
    private LinkType linkType;
    private String kind;
    private boolean isExplicitlyValidated;

    AccountingNoteType(AccountType accountType, NoteType noteType, Parent parent, LinkType linkType, String kind, boolean isExplicitlyValidated) {
        this.accountType = accountType;
        this.noteType = noteType;
        this.linkType = linkType;
        this.parent = parent;
        this.kind = kind;
        this.isExplicitlyValidated = isExplicitlyValidated;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public NoteType getNoteType() {
        return noteType;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public String getKind() {
        return kind;
    }

    public boolean isExplicitlyValidated() {
        return isExplicitlyValidated;
    }

    public Parent getParent() {
        return parent;
    }
}
