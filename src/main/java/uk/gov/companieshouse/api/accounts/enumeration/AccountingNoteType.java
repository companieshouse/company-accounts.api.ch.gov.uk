package uk.gov.companieshouse.api.accounts.enumeration;

import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;

public enum AccountingNoteType {

    SMALL_FULL_STOCKS(AccountType.SMALL_FULL, NoteType.STOCKS, SmallFullLinkType.STOCKS_NOTE,
                      "small-full-accounts#stocks-note", true),
    SMALL_FULL_DEBTORS(AccountType.SMALL_FULL, NoteType.DEBTORS, SmallFullLinkType.DEBTORS_NOTE,
                      "small-full-accounts-note#debtors", true);

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
