package uk.gov.companieshouse.api.accounts;

public enum PayableResource {

    CIC("cic.yaml");

    PayableResource(String yamlFile) {
        this.yamlFile = yamlFile;
    }

    private String yamlFile;

    public String getYamlFile() {

        return yamlFile;
    }
}
