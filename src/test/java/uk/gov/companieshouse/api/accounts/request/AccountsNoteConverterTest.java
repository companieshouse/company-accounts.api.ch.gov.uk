package uk.gov.companieshouse.api.accounts.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.enumeration.NoteType;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountsNoteConverterTest {

    private AccountingNoteType accountingNoteType = AccountingNoteType.SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS;

    private EnumMap<AccountType, EnumMap<NoteType, AccountingNoteType>> ACCOUNTS_NOTE_MAP = new EnumMap<>(AccountType.class);
    private EnumMap<NoteType, AccountingNoteType> ACCOUNTS_NOTE_TYPE = new EnumMap<>(NoteType.class);
    private NoteType noteType = NoteType.OFF_BALANCE_SHEET_ARRANGEMENTS;

    private AccountsNoteConverter converter;

    @BeforeEach
    private void setup() {

        ACCOUNTS_NOTE_TYPE.put(noteType, accountingNoteType);
        ACCOUNTS_NOTE_MAP.put(accountingNoteType.getAccountType(), ACCOUNTS_NOTE_TYPE);

        converter = new AccountsNoteConverter();
    }

    @Test
    @DisplayName("Get account type for the converter with the value from the map")
    void getAccountTypeForTheConverterSuccess() {

        assertEquals(accountingNoteType,
                ACCOUNTS_NOTE_TYPE.get(noteType));
    }
}