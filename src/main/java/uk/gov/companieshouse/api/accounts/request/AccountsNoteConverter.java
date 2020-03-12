package uk.gov.companieshouse.api.accounts.request;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.enumeration.NoteType;
import uk.gov.companieshouse.api.accounts.exception.InvalidPathParameterException;

import java.util.Arrays;
import java.util.EnumMap;

@Component
public class AccountsNoteConverter {

    private static final EnumMap<AccountType, EnumMap<NoteType, AccountingNoteType>> ACCOUNTS_NOTE_MAP = new EnumMap<>(AccountType.class);

    AccountsNoteConverter() {

        Arrays.stream(AccountingNoteType.values()).forEach(accountsNote -> {

            if(ACCOUNTS_NOTE_MAP.get(accountsNote.getAccountType()) == null) {

                ACCOUNTS_NOTE_MAP.put(accountsNote.getAccountType(), new EnumMap<NoteType, AccountingNoteType>(NoteType.class));
            }

            ACCOUNTS_NOTE_MAP.get(accountsNote.getAccountType()).put(accountsNote.getNoteType(), accountsNote);
        });
    }

    public AccountingNoteType getAccountsNote(AccountType accountType, NoteType noteType) {

        AccountingNoteType accountsNote = ACCOUNTS_NOTE_MAP.get(accountType).get(noteType);
        if (accountsNote == null) {
            throw new InvalidPathParameterException("No AccountsNote found for account type: " + accountType.toString() +
                    " and NoteType: " + noteType.toString());
        }
        return accountsNote;
    }
}
