package uk.gov.companieshouse.api.accounts.enumeration;

import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;

public enum AccountingNoteType {

    SMALL_FULL_ACCOUNTING_POLICIES(
                    AccountType.SMALL_FULL,
                    NoteType.ACCOUNTING_POLICIES,
                    SmallFullLinkType.ACCOUNTING_POLICY_NOTE,
                    "small-full-accounts-note#accounting-policy",
                    false),
    SMALL_FULL_EMPLOYEES(
                    AccountType.SMALL_FULL,
                    NoteType.EMPLOYEES,
                    SmallFullLinkType.EMPLOYEES_NOTE,
                    "small-full-accounts-note#employees",
                    true),
    SMALL_FULL_INTANGIBLE_ASSETS(
                    AccountType.SMALL_FULL,
                    NoteType.INTANGIBLE_ASSETS,
                    SmallFullLinkType.INTANGIBLE_ASSETS_NOTE,
                    "small-full-accounts-note#intangible-assets",
                    true),
    SMALL_FULL_TANGIBLE_ASSETS(
                    AccountType.SMALL_FULL,
                    NoteType.TANGIBLE_ASSETS,
                    SmallFullLinkType.TANGIBLE_ASSETS_NOTE,
                    "small-full-accounts-note#tangible-assets",
                    true),
    SMALL_FULL_FIXED_ASSETS_INVESTMENTS(
                    AccountType.SMALL_FULL,
                    NoteType.FIXED_ASSETS_INVESTMENTS,
                    SmallFullLinkType.FIXED_ASSETS_INVESTMENTS_NOTE,
                    "small-full-accounts-note#fixed-assets-investments",
                    true),
    SMALL_FULL_STOCKS(
                    AccountType.SMALL_FULL,
                    NoteType.STOCKS,
                    SmallFullLinkType.STOCKS_NOTE,
                    "small-full-accounts#stocks-note",
                    true),
    SMALL_FULL_DEBTORS(
                    AccountType.SMALL_FULL,
                    NoteType.DEBTORS,
                    SmallFullLinkType.DEBTORS_NOTE,
                    "small-full-accounts-note#debtors",
                    true),
    SMALL_FULL_CURRENT_ASSETS_INVESTMENTS(
                    AccountType.SMALL_FULL,
                    NoteType.CURRENT_ASSETS_INVESTMENTS,
                    SmallFullLinkType.CURRENT_ASSETS_INVESTMENTS_NOTE,
                    "small-full-accounts-note#current-assets-investments",
                    true),
    SMALL_FULL_CREDITORS_AFTER(
                    AccountType.SMALL_FULL,
                    NoteType.CREDITORS_AFTER,
                    SmallFullLinkType.CREDITORS_AFTER_MORE_THAN_ONE_YEAR_NOTE,
                    "small-full-accounts-note#creditors-after-one-year",
                    true),
    SMALL_FULL_CREDITORS_WITHIN(
                    AccountType.SMALL_FULL,
                    NoteType.CREDITORS_WITHIN,
                    SmallFullLinkType.CREDITORS_WITHIN_ONE_YEAR_NOTE,
                    "small-full-accounts-note#creditors-within-one-year",
                    true),
    SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS(
            AccountType.SMALL_FULL,
            NoteType.OFF_BALANCE_SHEET_ARRANGEMENTS,
            SmallFullLinkType.OFF_BALANCE_SHEET_ARRANGEMENTS_NOTE,
                    "small-full-accounts-note#off-balance-sheet-arrangements",
                    true);

    private AccountType accountType;
    private NoteType noteType;
    private LinkType linkType;
    private String kind;
    private boolean isExplicitlyValidated;

    AccountingNoteType(AccountType accountType, NoteType noteType, LinkType linkType, String kind, boolean isExplicitlyValidated) {
        this.accountType = accountType;
        this.noteType = noteType;
        this.linkType = linkType;
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
}
