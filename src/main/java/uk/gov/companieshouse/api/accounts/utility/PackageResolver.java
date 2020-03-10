package uk.gov.companieshouse.api.accounts.utility;

import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Component;

@Component
public class PackageResolver {

    public String getNotePackage(String accountType, String noteType) {

        return "." + getPackage(accountType) +
                ".notes" +
                "." + getPackage(noteType) +
                "." + getClassName(noteType);
    }

    private String getPackage(String input) {

        return input.replace("-", "").toLowerCase();
    }

    private String getClassName(String input) {

        return WordUtils.capitalizeFully(input.replace("-", " ")).replace(" ", "");
    }
}
