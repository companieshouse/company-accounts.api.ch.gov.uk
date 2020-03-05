package uk.gov.companieshouse.api.accounts.utility;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import uk.gov.companieshouse.api.accounts.model.AccountTypeConfig;
import uk.gov.companieshouse.api.accounts.model.YamlConfig;

import java.io.InputStream;
import java.util.Properties;

public class AccountNotePathsYamlReader {

    private static final String PATH_YAML_FILE = "paths.yml";

    private static final String PATH_PREFIX = "/transactions/{transactionId}/company-accounts/{companyAccountId}/{accountType:";

    private static final String PROPERTY_PREFIX = "controller.paths.";

    private Properties properties;

    public AccountNotePathsYamlReader(Properties properties) {
        this.properties = properties;
    }

    public void populatePropertiesFromYamlFile() {

        Yaml yaml = new Yaml();

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(PATH_YAML_FILE);

        YamlConfig yamlConfig = yaml.loadAs(inputStream, YamlConfig.class);

        yamlConfig.getAccountTypeConfig().keySet().forEach(accountType -> {

            AccountTypeConfig accountTypeConfig = yamlConfig.getAccountTypeConfig().get(accountType);


            if (accountTypeConfig.getNotes() != null) {
                addNotesPaths(accountType, accountTypeConfig, properties);
            }

        });
    }

    private void addNotesPaths(String accountType, AccountTypeConfig accountTypeConfig, Properties properties) {

        StringBuilder accountNotes = new StringBuilder(
                PATH_PREFIX + accountType + "}/notes/{resource:");

        for (int i = 0; i < accountTypeConfig.getNotes().size(); i++) {
            accountNotes.append(accountTypeConfig.getNotes().get(i));
            if (i < accountTypeConfig.getNotes().size() - 1) {
                accountNotes.append("|");
            }
        }
        accountNotes.append("}");

        properties.put(PROPERTY_PREFIX + normalizeAccountType(accountType) + ".notes", accountNotes.toString());
    }

    private String normalizeAccountType(String accountType) {

        return accountType.replace("-", "");
    }
}
