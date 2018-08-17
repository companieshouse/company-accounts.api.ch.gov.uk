package uk.gov.companieshouse.api.accounts.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Functionality to read the descriptions from the yml file. This has been copied from an existing
 * repository that will be deleted.
 *
 * This class will need to be placed in a util's repository and used as a library since it will be used
 * by different repositories.
 *
 */
@Component
public class DocumentDescriptionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);
    private static final String FILING_DESCRIPTIONS_FILE_NAME = "api-enumerations/filing_descriptions.yml";
    private static final String DESCRIPTION_IDENTIFIERS_KEY = "description_identifiers";

    public String getDescription(String accountType, Map<String, Object> parameters) throws IOException {
        Yaml yaml = new Yaml();
        File fileDescriptionsFile = new File(FILING_DESCRIPTIONS_FILE_NAME);
        if (fileDescriptionsFile.exists()) {
            InputStream inputStream = new FileInputStream(fileDescriptionsFile);

            Map<String, Object> filingDescriptions = (Map)yaml.load(inputStream);
            Map<String, Object> descriptionIdentifiers =
                (Map)getFilingDescriptionsValue(filingDescriptions, DESCRIPTION_IDENTIFIERS_KEY);

            String description =
                (String)getFilingDescriptionsValue(descriptionIdentifiers, accountType);

            return populateParameters(description, parameters);
        } else {
            Map<String, Object> dataMap = new HashMap();
            dataMap.put("file", FILING_DESCRIPTIONS_FILE_NAME);
            LOGGER.trace("File descriptions not found", dataMap);
            return null;
        }
    }

    private Object getFilingDescriptionsValue(Map<String, Object> map, String key) {
        if (map != null) {
            if (map.containsKey(key)) {
                return map.get(key);
            }

            Map<String, Object> dataMap = new HashMap();
            dataMap.put("file", FILING_DESCRIPTIONS_FILE_NAME);
            dataMap.put("key", key);
            LOGGER.trace("Value not found in file descriptions", dataMap);
        }

        return null;
    }

    private String populateParameters(String description, Map<String, Object> parameters) {
        StrSubstitutor sub = new StrSubstitutor(parameters, "{", "}");
        return sub.replace(description);
    }
}
