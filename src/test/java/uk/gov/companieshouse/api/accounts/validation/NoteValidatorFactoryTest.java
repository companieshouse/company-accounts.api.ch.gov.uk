package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.MissingInfrastructureException;
import uk.gov.companieshouse.api.accounts.model.rest.Note;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NoteValidatorFactoryTest {

    @Mock
    private NoteValidator<Note> noteValidator;

    private AccountingNoteType accountingNoteTypeWithCorrespondingValidator =
            AccountingNoteType.SMALL_FULL_STOCKS;

    private AccountingNoteType accountingNoteTypeWithoutCorrespondingValidator =
            AccountingNoteType.SMALL_FULL_DEBTORS;

    private NoteValidatorFactory<Note> factory;

    @BeforeEach
    private void setup() {
        when(noteValidator.getAccountingNoteType()).thenReturn(accountingNoteTypeWithCorrespondingValidator);
        List<NoteValidator<Note>> noteValidators = new ArrayList<>();
        noteValidators.add(noteValidator);
        factory = new NoteValidatorFactory<>(noteValidators);
    }

    @Test
    @DisplayName("Get note validator for accounting note type with corresponding validator")
    void getNoteValidatorForAccountingNoteTypeWithCorrespondingValidator() {

        assertEquals(noteValidator,
                factory.getValidator(accountingNoteTypeWithCorrespondingValidator));
    }

    @Test
    @DisplayName("Get note validator for accounting note type without corresponding validator")
    void getNoteValidatorForAccountingNoteTypeWithoutCorrespondingValidator() {

        assertThrows(MissingInfrastructureException.class,
                () -> factory.getValidator(accountingNoteTypeWithoutCorrespondingValidator));
    }
}
