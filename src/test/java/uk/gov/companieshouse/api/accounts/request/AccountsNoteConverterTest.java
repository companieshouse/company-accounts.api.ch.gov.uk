package uk.gov.companieshouse.api.accounts.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.enumeration.NoteType;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountsNoteConverterTest {

    private final AccountsNoteConverter converter = new AccountsNoteConverter();;

    @Test
    @DisplayName("Get account type for the converter with the value from the map")
    void getAccountTypeForTheConverterSuccess() {
        AccountingNoteType accountingNoteType = converter.getAccountsNote(AccountType.SMALL_FULL,
                NoteType.OFF_BALANCE_SHEET_ARRANGEMENTS);
        assertEquals(AccountingNoteType.SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS, accountingNoteType);
    }

    @Test
    @DisplayName("Get incorrect account type for the converter with the value from the map")
    void getIncorrectAccountTypeForTheConverterSuccess() {
        AccountingNoteType accountingNoteType = converter.getAccountsNote(AccountType.SMALL_FULL, NoteType.STOCKS);
        assertNotEquals(AccountingNoteType.SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS, accountingNoteType);
    }
}