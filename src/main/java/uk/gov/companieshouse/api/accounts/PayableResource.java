package uk.gov.companieshouse.api.accounts;

public enum PayableResource {

    CIC("cic-report");

    PayableResource(String resource) {
        this.resource = resource;
    }

    private String resource;

    public String getResource() {

        return resource;
    }
}
