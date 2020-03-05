package uk.gov.companieshouse.api.accounts.enumeration;

import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;

public enum AccountsNote {

    OFF_BALANCE_SHEET_ARRANGEMENTS(AccountType.SMALL_FULL, NoteType.OFF_BALANCE_SHEET_ARRANGEMENTS, Parent.SMALL_FULL, SmallFullLinkType.OFF_BALANCE_SHEET_ARRANGEMENTS_NOTE, "small-full-accounts-note#off-balance-sheet-arrangements");

    private AccountType accountType;
    private NoteType noteType;
    private Parent parent;
    private LinkType linkType;
    private String kind;


    AccountsNote(AccountType accountType, NoteType noteType, Parent parent, LinkType linkType, String kind) {

        this.accountType = accountType;
        this.noteType = noteType;
        this.parent = parent;
        this.linkType = linkType;
        this.kind = kind;

    }

    public AccountType getAccountType() {

        return accountType;

    }

    public NoteType getNoteType() {

        return noteType;

    }

    public Parent getParent() {

        return parent;

    }

    public LinkType getLinkType() {

        return linkType;

    }

    public String getKind() {

        return kind;

    }
}
