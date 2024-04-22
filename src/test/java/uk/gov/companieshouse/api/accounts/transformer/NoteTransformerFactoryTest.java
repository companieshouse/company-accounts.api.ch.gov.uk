package uk.gov.companieshouse.api.accounts.transformer;

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
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Note;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NoteTransformerFactoryTest {
    @Mock
    private NoteTransformer<Note, NoteEntity> noteTransformer;

    private final AccountingNoteType accountingNoteTypeWithCorrespondingTransformer =
            AccountingNoteType.SMALL_FULL_STOCKS;

    private final AccountingNoteType accountingNoteTypeWithoutCorrespondingTransformer =
            AccountingNoteType.SMALL_FULL_DEBTORS;

    private NoteTransformerFactory<Note, NoteEntity> factory;

    @BeforeEach
    public void setUp() {
        when(noteTransformer.getAccountingNoteType()).thenReturn(accountingNoteTypeWithCorrespondingTransformer);
        List<NoteTransformer<Note, NoteEntity>> noteTransformers = new ArrayList<>();
        noteTransformers.add(noteTransformer);
        factory = new NoteTransformerFactory<>(noteTransformers);
    }

    @Test
    @DisplayName("Get note transformer for accounting note type with corresponding transformer")
    void getNoteTransformerForAccountingNoteTypeWithCorrespondingTransformer() {
        assertEquals(noteTransformer, factory.getTransformer(accountingNoteTypeWithCorrespondingTransformer));
    }

    @Test
    @DisplayName("Get note transformer for accounting note type without corresponding transformer")
    void getNoteTransformerForAccountingNoteTypeWithoutCorrespondingTransformer() {
        assertThrows(MissingInfrastructureException.class,
                () -> factory.getTransformer(accountingNoteTypeWithoutCorrespondingTransformer));
    }
}
