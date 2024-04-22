package uk.gov.companieshouse.api.accounts.utility;

import java.util.List;
import uk.gov.companieshouse.api.accounts.model.AccountsNotes;

import java.util.Properties;

public class AccountsNotesPathsYamlReader {

    private static final String NOTES_YML_FILE = "notes.yml";

    private static final String NOTES_PATH_PREFIX = "/transactions/{transactionId}/company-accounts/{companyAccountId}/{accountType:";

    private static final String PROPERTY_PREFIX = "controller.paths.notes.";

    private final AccountsNotes accountsNotes;

    public AccountsNotesPathsYamlReader(YamlResourceMapper yamlResourceMapper) {
        this.accountsNotes = yamlResourceMapper.fetchObjectFromYaml(NOTES_YML_FILE, AccountsNotes.class);
    }

    public void populatePropertiesFromYamlFile(Properties properties) {
        accountsNotes.getNotes().keySet().forEach(accountType -> {
            List<String> notes = accountsNotes.getNotes().get(accountType);
            addNotesPaths(accountType, notes, properties);
        });
    }

    private void addNotesPaths(String accountType, List<String> notes, Properties properties) {
        StringBuilder accountNotes = new StringBuilder(NOTES_PATH_PREFIX + accountType + "}/notes/{noteType:");

        for (int i = 0; i < notes.size(); i++) {
            accountNotes.append(notes.get(i));
            if (i < notes.size() - 1) {
                accountNotes.append("|");
            }
        }
        accountNotes.append("}");

        properties.put(PROPERTY_PREFIX + normalizeAccountType(accountType), accountNotes.toString());
    }

    private String normalizeAccountType(String accountType) {
        return accountType.replace("-", "");
    }
}
