package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.MissingInfrastructureException;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

import java.util.EnumMap;
import java.util.List;

@Component
public class AccountsNoteRepositoryFactory<E extends NoteEntity> {

    private final EnumMap<AccountingNoteType, AccountsNoteRepository<E>> repositoryMap =
            new EnumMap<>(AccountingNoteType.class);

    @Autowired
    public AccountsNoteRepositoryFactory(List<AccountsNoteRepository<E>> repositoryList) {
        for (AccountsNoteRepository<E> repository : repositoryList) {
            if (repository.getAccountsNote() != null) {
                repositoryMap.put(repository.getAccountsNote(), repository);
            }
        }
    }

    public AccountsNoteRepository<E> getRepository(AccountingNoteType accountingNoteType) {
        AccountsNoteRepository<E> repository = repositoryMap.get(accountingNoteType);

        if (repository == null) {
            throw new MissingInfrastructureException(
                    "No repository type for accounts resource: " + accountingNoteType.toString());
        }
        return repository;
    }
}