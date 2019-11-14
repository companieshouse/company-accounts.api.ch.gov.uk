package uk.gov.companieshouse.api.accounts.enumeration;

public enum Resource {

    STOCKS("stocks"),
    TANGIBLE_ASSETS("tangible-assets"),
    DEBTORS("debtors");

    Resource(String name) { this.name = name; }

    private String name;

    public String getName() {
        return name;
    }
}
