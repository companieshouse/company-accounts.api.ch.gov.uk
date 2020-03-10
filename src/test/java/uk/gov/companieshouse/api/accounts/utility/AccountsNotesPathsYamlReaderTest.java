package uk.gov.companieshouse.api.accounts.utility;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.AccountsNotes;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountsNotesPathsYamlReaderTest {

    @Mock
    private YamlResourceMapper yamlResourceMapper;

    @Mock
    private AccountsNotes accountsNotes;

    @Mock
    private Properties properties;

    private AccountsNotesPathsYamlReader accountsNotesPathsYamlReader;

    private static final String NOTES_YAML_FILE = "notes.yml";

    private static final String ACCOUNT_TYPE_1 = "account-type-1";

    private static final String ACCOUNT_TYPE_1_NORMALIZED = "accounttype1";

    private static final String ACCOUNT_TYPE_2 = "account-type-2";

    private static final String ACCOUNT_TYPE_2_NORMALIZED = "accounttype2";

    private static final String ACCOUNT_TYPE_1_NOTE_1 = "account-type-1-note-1";

    private static final String ACCOUNT_TYPE_1_NOTE_2 = "account-type-1-note-2";

    private static final String ACCOUNT_TYPE_2_NOTE_1 = "account-type-2-note-1";

    private static final String PROPERTY_PREFIX = "controller.paths.notes.";

    private static final String NOTES_PATH_PREFIX = "/transactions/{transactionId}/company-accounts/{companyAccountId}/{accountType:";

    @BeforeEach
    private void setup() {

        when(yamlResourceMapper.fetchObjectFromYaml(NOTES_YAML_FILE, AccountsNotes.class))
                .thenReturn(accountsNotes);

        List<String> accountType1Notes = new ArrayList<>();
        accountType1Notes.add(ACCOUNT_TYPE_1_NOTE_1);
        accountType1Notes.add(ACCOUNT_TYPE_1_NOTE_2);

        List<String> accountType2Notes = new ArrayList<>();
        accountType2Notes.add(ACCOUNT_TYPE_2_NOTE_1);

        Map<String, List<String>> accountTypeNotes = new HashMap<>();
        accountTypeNotes.put(ACCOUNT_TYPE_1, accountType1Notes);
        accountTypeNotes.put(ACCOUNT_TYPE_2, accountType2Notes);

        when(accountsNotes.getNotes()).thenReturn(accountTypeNotes);

        accountsNotesPathsYamlReader = new AccountsNotesPathsYamlReader(yamlResourceMapper);
    }

    @Test
    @DisplayName("Populate properties from yaml file")
    void populatePropertiesFromYamlFile() {

        accountsNotesPathsYamlReader.populatePropertiesFromYamlFile(properties);

        verify(properties).put(PROPERTY_PREFIX + ACCOUNT_TYPE_1_NORMALIZED,
                NOTES_PATH_PREFIX + ACCOUNT_TYPE_1 + "}/notes/{noteType:" + ACCOUNT_TYPE_1_NOTE_1 + "|" + ACCOUNT_TYPE_1_NOTE_2 + "}");

        verify(properties).put(PROPERTY_PREFIX + ACCOUNT_TYPE_2_NORMALIZED,
                NOTES_PATH_PREFIX + ACCOUNT_TYPE_2 + "}/notes/{noteType:" + ACCOUNT_TYPE_2_NOTE_1 + "}");
    }
}
