package uk.gov.companieshouse.api.accounts.utility;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.yaml.snakeyaml.Yaml;
import uk.gov.companieshouse.api.accounts.model.AccountTypeConfig;
import uk.gov.companieshouse.api.accounts.model.YamlConfig;

public class AccountResourcePathsYamlReader {

    private static final String PATHS_YAML_FILE = "paths.yml";

    private static final String PATH_PREFIX = "/transactions/{transactionId}/company-accounts/{companyAccountId}/{accountType:";

    private Properties properties;

    public AccountResourcePathsYamlReader(Properties properties) {
        this.properties = properties;
    }

    public void populatePropertiesFromYamlFile() {

        List<String> controllerPaths = new ArrayList<>();

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(PATHS_YAML_FILE);

        YamlConfig yamlConfig = yaml.loadAs(inputStream, YamlConfig.class);

        yamlConfig.getAccountTypeConfig().keySet().forEach(accountType -> {

            AccountTypeConfig accountTypeConfig = yamlConfig.getAccountTypeConfig().get(accountType);

            if (accountTypeConfig.getSoloResources() != null) {
                addSoloResourcePaths(accountType, accountTypeConfig, controllerPaths);
            }

            if (accountTypeConfig.getNotes() != null) {
                addNotesPaths(accountType, accountTypeConfig, controllerPaths);
            }

            if (accountTypeConfig.getCurrentPeriod() != null) {
                addCurrentPeriodPaths(accountType, accountTypeConfig, controllerPaths);
            }
        });

        properties.put("controller.paths", getFormattedControllerPaths(controllerPaths));
    }

    private void addSoloResourcePaths(String accountType, AccountTypeConfig accountTypeConfig, List<String> controllerPaths) {

        StringBuilder accountSoloResources = new StringBuilder(
                PATH_PREFIX + accountType + "}/{resource:");

        for (int i = 0; i < accountTypeConfig.getSoloResources().size(); i++) {
            accountSoloResources.append(accountTypeConfig.getSoloResources().get(i));
            if (i < accountTypeConfig.getSoloResources().size() - 1) {
                accountSoloResources.append("|");
            }
        }
        accountSoloResources.append("}");

        controllerPaths.add(accountSoloResources.toString());
    }

    private void addNotesPaths(String accountType, AccountTypeConfig accountTypeConfig, List<String> controllerPaths) {

        StringBuilder accountNotes = new StringBuilder(
                PATH_PREFIX + accountType + "}/notes/{resource:");

        for (int i = 0; i < accountTypeConfig.getNotes().size(); i++) {
            accountNotes.append(accountTypeConfig.getNotes().get(i));
            if (i < accountTypeConfig.getNotes().size() - 1) {
                accountNotes.append("|");
            }
        }
        accountNotes.append("}");

        controllerPaths.add(accountNotes.toString());
    }

    private void addCurrentPeriodPaths(String accountType, AccountTypeConfig accountTypeConfig, List<String> controllerPaths) {

        StringBuilder currentPeriod = new StringBuilder(
                PATH_PREFIX + accountType + "}/{period:current-period}/{resource:");

        for (int i = 0; i < accountTypeConfig.getCurrentPeriod().size(); i++) {
            currentPeriod.append(accountTypeConfig.getCurrentPeriod().get(i));
            if (i < accountTypeConfig.getCurrentPeriod().size() - 1) {
                currentPeriod.append("|");
            }
        }
        currentPeriod.append("}");

        controllerPaths.add(currentPeriod.toString());
    }

    private String getFormattedControllerPaths(List<String> controllerPaths) {

        StringBuilder formattedControllerPaths = new StringBuilder();
        for (int i = 0; i < controllerPaths.size(); i++) {
            formattedControllerPaths.append(controllerPaths.get(i));
            if (i < controllerPaths.size() - 1) {
                formattedControllerPaths.append(", ");
            }
        }
        return formattedControllerPaths.toString();
    }
}
