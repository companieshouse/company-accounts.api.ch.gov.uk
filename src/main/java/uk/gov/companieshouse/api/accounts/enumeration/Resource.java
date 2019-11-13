package uk.gov.companieshouse.api.accounts.enumeration;

import java.util.Arrays;

public enum Resource {

    STOCKS("stocks"),
    TANGIBLE_ASSETS("tangible-assets");

    private String name;

    public String getName() {
        return name;
    }

    Resource(String name) {

        this.name = name;
    }

    public static Resource fromString(String resource) {
        return Arrays.stream(values())
                .filter(resourceName -> resourceName.getName().equals(resource))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown resource name: " + resource));
    }
}
