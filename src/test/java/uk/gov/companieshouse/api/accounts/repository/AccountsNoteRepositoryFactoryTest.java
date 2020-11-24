package uk.gov.companieshouse.api.accounts.repository;

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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountsNoteRepositoryFactoryTest {

    @Mock
    private AccountsNoteRepository<NoteEntity> accountsNoteRepository;

    private AccountingNoteType accountingNoteType = AccountingNoteType.SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS;

    private AccountingNoteType accountingNoteTypeWithoutRepository = AccountingNoteType.SMALL_FULL_DEBTORS;

    private AccountsNoteRepositoryFactory<NoteEntity> repositoryFactory;

    @BeforeEach
    private void setup() {

        when(accountsNoteRepository.getAccountsNote()).thenReturn(accountingNoteType);

        List<AccountsNoteRepository<NoteEntity>> noteRepositories = new ArrayList<>();
        noteRepositories.add(accountsNoteRepository);
        repositoryFactory = new AccountsNoteRepositoryFactory<>(noteRepositories);
    }

    @Test
    @DisplayName("Get note repository for accounting note type with corresponding repository")
    void getNoteRepositoryForAccountingNoteTypeWithCorrespondingRepository() {

        assertEquals(accountsNoteRepository,
                repositoryFactory.getRepository(accountingNoteType));
    }

    @Test
    @DisplayName("Get note repository for accounting note type without corresponding repository")
    void getNoteRepositoryForAccountingNoteTypeWithoutCorrespondingRepository() {

        assertThrows(MissingInfrastructureException.class,
                () -> repositoryFactory.getRepository(accountingNoteTypeWithoutRepository));
    }

}