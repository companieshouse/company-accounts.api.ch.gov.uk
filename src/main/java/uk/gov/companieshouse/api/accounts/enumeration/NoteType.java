package uk.gov.companieshouse.api.accounts.enumeration;

public enum NoteType {

    STOCKS("stocks"),
    DEBTORS("debtors");

    NoteType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }
}
