package uk.gov.companieshouse.api.accounts.utility;

import java.io.InputStream;
import java.util.Properties;
import org.yaml.snakeyaml.Yaml;
import uk.gov.companieshouse.api.accounts.model.AccountTypeConfig;
import uk.gov.companieshouse.api.accounts.model.YamlConfig;

public class AccountResourcePathsYamlReader {

    private static final String PATHS_YAML_FILE = "paths.yml";

    private static final String PATH_PREFIX = "/transactions/{transactionId}/company-accounts/{companyAccountId}/{accountType:";

    private static final String PROPERTY_PREFIX = "controller.paths.";

    private Properties properties;

    public AccountResourcePathsYamlReader(Properties properties) {
        this.properties = properties;
    }

    public void populatePropertiesFromYamlFile() {

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(PATHS_YAML_FILE);

        YamlConfig yamlConfig = yaml.loadAs(inputStream, YamlConfig.class);

        yamlConfig.getAccountTypeConfig().keySet().forEach(accountType -> {

            AccountTypeConfig accountTypeConfig = yamlConfig.getAccountTypeConfig().get(accountType);

            if (accountTypeConfig.getSoloResources() != null) {
                addSoloResourcePaths(accountType, accountTypeConfig, properties);
            }

            if (accountTypeConfig.getNotes() != null) {
                addNotesPaths(accountType, accountTypeConfig, properties);
            }

            if (accountTypeConfig.getCurrentPeriod() != null) {
                addCurrentPeriodPaths(accountType, accountTypeConfig, properties);
            }

            if (accountTypeConfig.getPreviousPeriod() != null) {
                addPreviousPeriodPaths(accountType, accountTypeConfig, properties);
            }
        });
    }

    private void addSoloResourcePaths(String accountType, AccountTypeConfig accountTypeConfig, Properties properties) {

        StringBuilder accountSoloResources = new StringBuilder(
                PATH_PREFIX + accountType + "}/{resource:");

        for (int i = 0; i < accountTypeConfig.getSoloResources().size(); i++) {
            accountSoloResources.append(accountTypeConfig.getSoloResources().get(i));
            if (i < accountTypeConfig.getSoloResources().size() - 1) {
                accountSoloResources.append("|");
            }
        }
        accountSoloResources.append("}");

        properties.put(PROPERTY_PREFIX + normalizeAccountType(accountType) + ".solo", accountSoloResources.toString());
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

    private void addCurrentPeriodPaths(String accountType, AccountTypeConfig accountTypeConfig, Properties properties) {

        StringBuilder currentPeriod = new StringBuilder(
                PATH_PREFIX + accountType + "}/{period:current-period}/{resource:");

        for (int i = 0; i < accountTypeConfig.getCurrentPeriod().size(); i++) {
            currentPeriod.append(accountTypeConfig.getCurrentPeriod().get(i));
            if (i < accountTypeConfig.getCurrentPeriod().size() - 1) {
                currentPeriod.append("|");
            }
        }
        currentPeriod.append("}");

        properties.put(PROPERTY_PREFIX + normalizeAccountType(accountType) + ".current", currentPeriod.toString());
    }

    private void addPreviousPeriodPaths(String accountType, AccountTypeConfig accountTypeConfig, Properties properties) {

        StringBuilder previousPeriod = new StringBuilder(
                PATH_PREFIX + accountType + "}/{period:previous-period}/{resource:");

        for (int i = 0; i < accountTypeConfig.getPreviousPeriod().size(); i++) {
            previousPeriod.append(accountTypeConfig.getPreviousPeriod().get(i));
            if (i < accountTypeConfig.getPreviousPeriod().size() - 1) {
                previousPeriod.append("|");
            }
        }
        previousPeriod.append("}");

        properties.put(PROPERTY_PREFIX + normalizeAccountType(accountType) + ".previous", previousPeriod.toString());
    }

    private String normalizeAccountType(String accountType) {

        return accountType.replace("-", "");
    }
}
