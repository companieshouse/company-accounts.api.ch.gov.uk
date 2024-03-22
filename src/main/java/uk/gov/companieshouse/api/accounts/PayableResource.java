package uk.gov.companieshouse.api.accounts;

public enum PayableResource {

    CIC("cic-report");

    PayableResource(String resource) {
        this.resource = resource;
    }

    private final String resource;

    public String getResource() {

        return resource;
    }
}
