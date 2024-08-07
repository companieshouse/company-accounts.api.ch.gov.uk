package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.parent.ParentResource;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;
import uk.gov.companieshouse.api.accounts.repository.AccountsNoteRepository;
import uk.gov.companieshouse.api.accounts.repository.AccountsNoteRepositoryFactory;
import uk.gov.companieshouse.api.accounts.service.NoteService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.NoteTransformer;
import uk.gov.companieshouse.api.accounts.transformer.NoteTransformerFactory;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.NoteValidator;
import uk.gov.companieshouse.api.accounts.validation.NoteValidatorFactory;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NoteServiceImplTest {
    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private NoteValidatorFactory<Note> validatorFactory;
    
    @Mock
    private NoteValidator<Note> validator;

    @Mock
    private NoteTransformerFactory<Note, NoteEntity> transformerFactory;
    
    @Mock
    private NoteTransformer<Note, NoteEntity> transformer;

    @Mock
    private ParentResourceFactory<LinkType> parentResourceFactory;
    
    @Mock
    private ParentResource<LinkType> parentResource;
    
    @Mock
    private Note note;
    
    @Mock
    private NoteEntity noteEntity;
    
    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Errors errors;

    @Mock
    private AccountsNoteRepositoryFactory<NoteEntity> repositoryFactory;

    @Mock
    private AccountsNoteRepository<NoteEntity> accountsNoteRepository;

    @InjectMocks
    private NoteService noteService = new NoteServiceImpl();

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "selfLink";
    private final AccountingNoteType accountingNoteType = AccountingNoteType.SMALL_FULL_DEBTORS;

    @Test
    @DisplayName("Create - success - with explicit validation")
    void createSuccessWithExplicitValidation() throws DataException {
        when(repositoryFactory.getRepository(accountingNoteType)).thenReturn(accountsNoteRepository);

        AccountingNoteType accountingNoteTypeWithExplicitValidation = AccountingNoteType.SMALL_FULL_DEBTORS;
        
        when(validatorFactory.getValidator(accountingNoteTypeWithExplicitValidation)).thenReturn(validator);
        
        when(validator.validateSubmission(note, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        
        when(errors.hasErrors()).thenReturn(false);
        
        when(request.getRequestURI()).thenReturn(SELF_LINK);
        
        when(transformerFactory.getTransformer(accountingNoteTypeWithExplicitValidation))
                .thenReturn(transformer);
        
        when(transformer.transform(note)).thenReturn(noteEntity);
        
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + accountingNoteTypeWithExplicitValidation.getNoteType().getType())).thenReturn(GENERATED_ID);
        
        when(parentResourceFactory.getParentResource(accountingNoteTypeWithExplicitValidation.getAccountType()))
                .thenReturn(parentResource);

        ResponseObject<Note> response = noteService.create(
                note, accountingNoteTypeWithExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request);
        
        assertMetaDataSetOnRestObject(accountingNoteTypeWithExplicitValidation);
        assertIdSetOnEntity();
        verify(repositoryFactory.getRepository(accountingNoteType)).insert(noteEntity);
        verify(parentResource).addLink(
                 COMPANY_ACCOUNTS_ID, accountingNoteTypeWithExplicitValidation.getLinkType(), SELF_LINK, request);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(note, response.getData());
    }

    @Test
    @DisplayName("Create - success - without explicit validation")
    void createSuccessWithoutExplicitValidation() throws DataException {
        AccountingNoteType accountingNoteTypeWithoutExplicitValidation = AccountingNoteType.SMALL_FULL_ACCOUNTING_POLICIES;

        when(repositoryFactory.getRepository(accountingNoteTypeWithoutExplicitValidation))
                .thenReturn(accountsNoteRepository);

        when(request.getRequestURI()).thenReturn(SELF_LINK);

        when(transformerFactory.getTransformer(accountingNoteTypeWithoutExplicitValidation)).thenReturn(transformer);

        when(transformer.transform(note)).thenReturn(noteEntity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + accountingNoteTypeWithoutExplicitValidation.getNoteType().getType())).thenReturn(GENERATED_ID);

        when(parentResourceFactory.getParentResource(accountingNoteTypeWithoutExplicitValidation.getAccountType()))
                .thenReturn(parentResource);

        ResponseObject<Note> response = noteService.create(note, accountingNoteTypeWithoutExplicitValidation,
                transaction, COMPANY_ACCOUNTS_ID, request);

        verify(validatorFactory, never()).getValidator(accountingNoteTypeWithoutExplicitValidation);
        assertMetaDataSetOnRestObject(accountingNoteTypeWithoutExplicitValidation);
        assertIdSetOnEntity();
        verify(repositoryFactory.getRepository(accountingNoteTypeWithoutExplicitValidation)).insert(noteEntity);
        verify(parentResource).addLink(COMPANY_ACCOUNTS_ID, accountingNoteTypeWithoutExplicitValidation.getLinkType(),
                SELF_LINK, request);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(note, response.getData());
    }

    @Test
    @DisplayName("Create - has validation errors")
    void createHasValidationErrors() throws DataException {
        AccountingNoteType accountingNoteTypeWithExplicitValidation = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(validatorFactory.getValidator(accountingNoteTypeWithExplicitValidation)).thenReturn(validator);

        when(validator.validateSubmission(note, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        when(errors.hasErrors()).thenReturn(true);

        ResponseObject<Note> response = noteService.create(
                note, accountingNoteTypeWithExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(transformerFactory, never()).getTransformer(accountingNoteTypeWithExplicitValidation);
        assertEquals(ResponseStatus.VALIDATION_ERROR, response.getStatus());
        assertEquals(errors, response.getErrors());
    }

    @Test
    @DisplayName("Create - throws DuplicateKeyException")
    void createThrowsDuplicateKeyException() throws DataException {
        AccountingNoteType accountingNoteTypeWithExplicitValidation = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(repositoryFactory.getRepository(accountingNoteTypeWithExplicitValidation))
                .thenReturn(accountsNoteRepository);

        when(validatorFactory.getValidator(accountingNoteTypeWithExplicitValidation)).thenReturn(validator);

        when(validator.validateSubmission(note, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        when(errors.hasErrors()).thenReturn(false);

        when(request.getRequestURI()).thenReturn(SELF_LINK);

        when(transformerFactory.getTransformer(accountingNoteTypeWithExplicitValidation)).thenReturn(transformer);

        when(transformer.transform(note)).thenReturn(noteEntity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + accountingNoteTypeWithExplicitValidation.getNoteType().getType())).thenReturn(GENERATED_ID);

        when(repositoryFactory.getRepository(accountingNoteTypeWithExplicitValidation).insert(noteEntity))
                .thenThrow(DuplicateKeyException.class);

        ResponseObject<Note> response = noteService.create(
                note, accountingNoteTypeWithExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(parentResource, never()).addLink(
                COMPANY_ACCOUNTS_ID, accountingNoteTypeWithExplicitValidation.getLinkType(), SELF_LINK, request);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
    }

    @Test
    @DisplayName("Create - throws MongoException")
    void createThrowsMongoException() throws DataException {
        AccountingNoteType accountingNoteTypeWithExplicitValidation = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(repositoryFactory.getRepository(accountingNoteTypeWithExplicitValidation))
                .thenReturn(accountsNoteRepository);

        when(validatorFactory.getValidator(accountingNoteTypeWithExplicitValidation)).thenReturn(validator);

        when(validator.validateSubmission(note, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        when(errors.hasErrors()).thenReturn(false);

        when(request.getRequestURI()).thenReturn(SELF_LINK);

        when(transformerFactory.getTransformer(accountingNoteTypeWithExplicitValidation)).thenReturn(transformer);

        when(transformer.transform(note)).thenReturn(noteEntity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + accountingNoteTypeWithExplicitValidation.getNoteType().getType())).thenReturn(GENERATED_ID);

        when(repositoryFactory.getRepository(accountingNoteTypeWithExplicitValidation).insert(noteEntity))
                .thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> noteService.create(
                note, accountingNoteTypeWithExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request));

        verify(parentResource, never()).addLink(
                COMPANY_ACCOUNTS_ID, accountingNoteTypeWithExplicitValidation.getLinkType(), SELF_LINK, request);
    }

    @Test
    @DisplayName("Update - success - with explicit validation")
    void updateSuccessWithExplicitValidation() throws DataException {
        AccountingNoteType accountingNoteTypeWithExplicitValidation = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(repositoryFactory.getRepository(accountingNoteTypeWithExplicitValidation)).thenReturn(accountsNoteRepository);

        when(validatorFactory.getValidator(accountingNoteTypeWithExplicitValidation)).thenReturn(validator);

        when(validator.validateSubmission(note, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        when(errors.hasErrors()).thenReturn(false);

        when(request.getRequestURI()).thenReturn(SELF_LINK);

        when(transformerFactory.getTransformer(accountingNoteTypeWithExplicitValidation)).thenReturn(transformer);

        when(transformer.transform(note)).thenReturn(noteEntity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + accountingNoteTypeWithExplicitValidation.getNoteType().getType())).thenReturn(GENERATED_ID);

        ResponseObject<Note> response = noteService.update(
                        note, accountingNoteTypeWithExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject(accountingNoteTypeWithExplicitValidation);
        assertIdSetOnEntity();
        verify(repositoryFactory.getRepository(accountingNoteTypeWithExplicitValidation)).save(noteEntity);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
    }

    @Test
    @DisplayName("Update - success - without explicit validation")
    void updateSuccessWithoutExplicitValidation() throws DataException {
        AccountingNoteType accountingNoteTypeWithoutExplicitValidation =
                AccountingNoteType.SMALL_FULL_ACCOUNTING_POLICIES;

        when(repositoryFactory.getRepository(accountingNoteTypeWithoutExplicitValidation))
                .thenReturn(accountsNoteRepository);

        when(request.getRequestURI()).thenReturn(SELF_LINK);

        when(transformerFactory.getTransformer(accountingNoteTypeWithoutExplicitValidation)).thenReturn(transformer);

        when(transformer.transform(note)).thenReturn(noteEntity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + accountingNoteTypeWithoutExplicitValidation.getNoteType().getType())).thenReturn(GENERATED_ID);

        ResponseObject<Note> response = noteService.update(
                note, accountingNoteTypeWithoutExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(validatorFactory, never()).getValidator(accountingNoteTypeWithoutExplicitValidation);
        assertMetaDataSetOnRestObject(accountingNoteTypeWithoutExplicitValidation);
        assertIdSetOnEntity();
        verify(repositoryFactory.getRepository(accountingNoteTypeWithoutExplicitValidation)).save(noteEntity);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
    }

    @Test
    @DisplayName("Update - has validation errors")
    void updateHasValidationErrors() throws DataException {
        AccountingNoteType accountingNoteTypeWithExplicitValidation =
                AccountingNoteType.SMALL_FULL_DEBTORS;

        when(validatorFactory.getValidator(accountingNoteTypeWithExplicitValidation)).thenReturn(validator);

        when(validator.validateSubmission(note, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        when(errors.hasErrors()).thenReturn(true);

        ResponseObject<Note> response = noteService.update(
                note, accountingNoteTypeWithExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(transformerFactory, never()).getTransformer(accountingNoteTypeWithExplicitValidation);
        assertEquals(ResponseStatus.VALIDATION_ERROR, response.getStatus());
        assertEquals(errors, response.getErrors());
    }

    @Test
    @DisplayName("Update - throws MongoException")
    void updateThrowsMongoException() throws DataException {
        AccountingNoteType accountingNoteTypeWithExplicitValidation = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(repositoryFactory.getRepository(accountingNoteTypeWithExplicitValidation))
                .thenReturn(accountsNoteRepository);

        when(validatorFactory.getValidator(accountingNoteTypeWithExplicitValidation))
                .thenReturn(validator);

        when(validator.validateSubmission(note, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);

        when(errors.hasErrors()).thenReturn(false);

        when(request.getRequestURI()).thenReturn(SELF_LINK);

        when(transformerFactory.getTransformer(accountingNoteTypeWithExplicitValidation))
                .thenReturn(transformer);

        when(transformer.transform(note)).thenReturn(noteEntity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + accountingNoteTypeWithExplicitValidation.getNoteType().getType())).thenReturn(GENERATED_ID);

        when(repositoryFactory.getRepository(accountingNoteTypeWithExplicitValidation).save(noteEntity))
                .thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> noteService.update(
                note, accountingNoteTypeWithExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Find - success")
    void findSuccess() throws DataException {
        AccountingNoteType accountingNoteType = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(repositoryFactory.getRepository(accountingNoteType)).thenReturn(accountsNoteRepository);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + accountingNoteType.getNoteType().getType()))
                .thenReturn(GENERATED_ID);

        when(repositoryFactory.getRepository(accountingNoteType).findById(GENERATED_ID))
                .thenReturn(Optional.of(noteEntity));

        when(transformerFactory.getTransformer(accountingNoteType)).thenReturn(transformer);

        when(transformer.transform(noteEntity)).thenReturn(note);

        ResponseObject<Note> response = noteService.find(accountingNoteType, COMPANY_ACCOUNTS_ID);

        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(note, response.getData());
    }

    @Test
    @DisplayName("Find - not found")
    void findNotFound() throws DataException {
        AccountingNoteType accountingNoteType = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(repositoryFactory.getRepository(accountingNoteType)).thenReturn(accountsNoteRepository);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + accountingNoteType.getNoteType().getType()))
                .thenReturn(GENERATED_ID);

        when(repositoryFactory.getRepository(accountingNoteType).findById(GENERATED_ID)).thenReturn(Optional.empty());

        ResponseObject<Note> response = noteService.find(accountingNoteType, COMPANY_ACCOUNTS_ID);

        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Find - throws MongoException")
    void findThrowsMongoException() {
        AccountingNoteType accountingNoteType = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(repositoryFactory.getRepository(accountingNoteType)).thenReturn(accountsNoteRepository);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + accountingNoteType.getNoteType().getType()))
                .thenReturn(GENERATED_ID);

        when(repositoryFactory.getRepository(accountingNoteType).findById(GENERATED_ID))
                .thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> noteService.find(accountingNoteType, COMPANY_ACCOUNTS_ID));
    }

    @Test
    @DisplayName("Delete - success")
    void deleteSuccess() throws DataException {
        AccountingNoteType accountingNoteType = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(repositoryFactory.getRepository(accountingNoteType)).thenReturn(accountsNoteRepository);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + accountingNoteType.getNoteType().getType()))
                .thenReturn(GENERATED_ID);

        when(repositoryFactory.getRepository(accountingNoteType).existsById(GENERATED_ID)).thenReturn(true);

        when(parentResourceFactory.getParentResource(accountingNoteType.getAccountType())).thenReturn(parentResource);

        ResponseObject<Note> response = noteService.delete(accountingNoteType, COMPANY_ACCOUNTS_ID, request);

        verify(repositoryFactory.getRepository(accountingNoteType)).deleteById(GENERATED_ID);
        verify(parentResource).removeLink(COMPANY_ACCOUNTS_ID, accountingNoteType.getLinkType(), request);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
    }

    @Test
    @DisplayName("Delete - not found")
    void deleteNotFound() throws DataException {
        AccountingNoteType accountingNoteType = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(repositoryFactory.getRepository(accountingNoteType)).thenReturn(accountsNoteRepository);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + accountingNoteType.getNoteType().getType()))
                .thenReturn(GENERATED_ID);

        when(repositoryFactory.getRepository(accountingNoteType).existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<Note> response = noteService.delete(accountingNoteType, COMPANY_ACCOUNTS_ID, request);

        verify(repositoryFactory.getRepository(accountingNoteType), never()).deleteById(GENERATED_ID);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    @DisplayName("Delete - throws MongoException")
    void deleteThrowsMongoException() {
        AccountingNoteType accountingNoteType = AccountingNoteType.SMALL_FULL_DEBTORS;

        when(repositoryFactory.getRepository(accountingNoteType)).thenReturn(accountsNoteRepository);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + accountingNoteType.getNoteType().getType()))
                .thenReturn(GENERATED_ID);

        when(repositoryFactory.getRepository(accountingNoteType).existsById(GENERATED_ID))
                .thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> noteService.delete(accountingNoteType, COMPANY_ACCOUNTS_ID, request));
    }

    private void assertMetaDataSetOnRestObject(AccountingNoteType accountingNoteType) {
        verify(note).setKind(accountingNoteType.getKind());
        verify(note).setEtag(anyString());
        verify(note).setLinks(anyMap());
    }

    private void assertIdSetOnEntity() {
        verify(noteEntity).setId(GENERATED_ID);
    }
}