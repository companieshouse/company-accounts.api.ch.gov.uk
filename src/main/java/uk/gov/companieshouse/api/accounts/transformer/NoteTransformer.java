package uk.gov.companieshouse.api.accounts.transformer;

import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Note;

public interface NoteTransformer<N extends Note, E extends NoteEntity> extends GenericTransformer<N, E> {

    AccountingNoteType getAccountingNoteType();
}
