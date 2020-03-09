package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

@Component
public interface AccountsNoteRepository<E extends NoteEntity> extends MongoRepository<E, String> {

    default AccountingNoteType getAccountsNote() {
        return null;
    }

}
