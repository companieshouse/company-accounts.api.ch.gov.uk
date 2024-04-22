package uk.gov.companieshouse.api.accounts.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.enumeration.NoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.parent.ParentResource;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;
import uk.gov.companieshouse.api.accounts.request.AccountTypeConverter;
import uk.gov.companieshouse.api.accounts.request.AccountsNoteConverter;
import uk.gov.companieshouse.api.accounts.service.impl.NoteServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NoteControllerTest {
    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private Note note;

    @Mock
    private AccountsNoteConverter accountsNoteConverter;

    @Mock
    private NoteServiceImpl noteService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private ParentResourceFactory<LinkType> parentResourceFactory;

    @Mock
    private ParentResource<LinkType> parentResource;

    @InjectMocks
    private NoteController noteController;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final AccountType accountType = AccountType.SMALL_FULL;

    private static final NoteType noteType = NoteType.OFF_BALANCE_SHEET_ARRANGEMENTS;

    private static final AccountingNoteType accountingNoteTypeWithExplicitValidation = AccountingNoteType.SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS;


    @Test
    @DisplayName("Note resource created successfully")
    void createNoteResourceSuccess() throws DataException{
        when(bindingResult.hasErrors()).thenReturn(false);

        when(accountsNoteConverter.getAccountsNote(accountType, noteType)).thenReturn(accountingNoteTypeWithExplicitValidation);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<Note> responseObject = new ResponseObject<>(ResponseStatus.CREATED, note);

        when(noteService.create(note, accountingNoteTypeWithExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity<Note> responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());

        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors())).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = noteController.create(note, bindingResult, COMPANY_ACCOUNTS_ID, accountType, noteType, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(note, responseEntity.getBody());
    }

    @Test
    @DisplayName("Create Note resource has failed - data exception thrown")
    void createNoteResourceDataException() throws DataException {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(accountsNoteConverter.getAccountsNote(accountType, noteType)).thenReturn(accountingNoteTypeWithExplicitValidation);
        when(noteService.create(note, accountingNoteTypeWithExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity<Object> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(apiResponseMapper.getErrorResponse())
                .thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                noteController.create(note, bindingResult, COMPANY_ACCOUNTS_ID, accountType, noteType,
                         request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Create Note resource - has binding errors")
    void createNoteResourceErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(eq(bindingResult), anyString())).thenReturn(new Errors());

        ResponseEntity<?> responseEntity =
                noteController.create(note, bindingResult, COMPANY_ACCOUNTS_ID, accountType, noteType, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Update Note resource - no parent link")
    void updateNoteResourceNoParentLink() {
        when(accountsNoteConverter.getAccountsNote(accountType, noteType)).thenReturn(accountingNoteTypeWithExplicitValidation);
        when(parentResourceFactory.getParentResource(accountType)).thenReturn(parentResource);

        ResponseEntity<?> responseEntity = noteController.update(note, bindingResult, COMPANY_ACCOUNTS_ID, accountType,
                noteType, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update Note resource - has binding errors")
    void updateNoteResourceBindingErrors() {
        when(accountsNoteConverter.getAccountsNote(accountType, noteType)).thenReturn(accountingNoteTypeWithExplicitValidation);
        when(parentResourceFactory.getParentResource(accountType)).thenReturn(parentResource);
        when(parentResource.childExists(request, accountingNoteTypeWithExplicitValidation.getLinkType())).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(eq(bindingResult), anyString())).thenReturn(new Errors());
        when(parentResourceFactory.getParentResource(accountType)).thenReturn(parentResource);

        ResponseEntity<?> responseEntity =
                noteController.update(note, bindingResult,
                        COMPANY_ACCOUNTS_ID, accountType, noteType, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update Note resource - success")
    void updateNoteResourceSuccess() throws DataException {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(parentResourceFactory.getParentResource(accountType)).thenReturn(parentResource);
        when(accountsNoteConverter.getAccountsNote(accountType, noteType)).thenReturn(accountingNoteTypeWithExplicitValidation);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(parentResource.childExists(request, accountingNoteTypeWithExplicitValidation.getLinkType())).thenReturn(true);

        ResponseObject<Note> responseObject = new ResponseObject<>(ResponseStatus.UPDATED,
                note);

        when(noteService.update(note, accountingNoteTypeWithExplicitValidation, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = noteController.update(note, bindingResult, COMPANY_ACCOUNTS_ID,
                accountType, noteType, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update Note resource - data exception thrown")
    void updateNoteResourceDataException() throws DataException {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountsNoteConverter.getAccountsNote(accountType, noteType)).thenReturn(accountingNoteTypeWithExplicitValidation);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(parentResourceFactory.getParentResource(accountType)).thenReturn(parentResource);
        when(parentResource.childExists(request, accountingNoteTypeWithExplicitValidation.getLinkType())).thenReturn(true);

        when(noteService.update(note, accountingNoteTypeWithExplicitValidation, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity<Object> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                noteController.update(note, bindingResult,
                        COMPANY_ACCOUNTS_ID, accountType, noteType, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Get Note resource - success")
    void getNoteResourceSuccess() throws DataException {
        when(accountsNoteConverter.getAccountsNote(accountType, noteType)).thenReturn(accountingNoteTypeWithExplicitValidation);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<Note> responseObject = new ResponseObject<>(ResponseStatus.FOUND, note);

        when(noteService.find(accountingNoteTypeWithExplicitValidation, COMPANY_ACCOUNTS_ID))
                .thenReturn(responseObject);

        ResponseEntity<Note> responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                noteController.get(COMPANY_ACCOUNTS_ID, accountType, noteType, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(note, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get Note resource - data exception thrown")
    void getNoteResourceDataException() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(accountsNoteConverter.getAccountsNote(accountType, noteType)).thenReturn(accountingNoteTypeWithExplicitValidation);

        DataException dataException = new DataException("");
        when(noteService.find(accountingNoteTypeWithExplicitValidation, COMPANY_ACCOUNTS_ID)).thenThrow(dataException);

        ResponseEntity<Object> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = noteController.get(COMPANY_ACCOUNTS_ID, accountType, noteType, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Delete Note resource - success")
    void deleteNoteResourceSuccess() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(accountsNoteConverter.getAccountsNote(accountType, noteType)).thenReturn(accountingNoteTypeWithExplicitValidation);

        ResponseObject<Note> responseObject = new ResponseObject<>(ResponseStatus.UPDATED,
                note);

        when(noteService.delete(accountingNoteTypeWithExplicitValidation, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = noteController.delete(COMPANY_ACCOUNTS_ID, accountType, noteType, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Delete Note resource - data exception thrown")
    void deleteNoteResourceDataException() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(accountsNoteConverter.getAccountsNote(accountType, noteType)).thenReturn(accountingNoteTypeWithExplicitValidation);
        when(noteService.delete(accountingNoteTypeWithExplicitValidation, COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity<Object> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = noteController.delete(COMPANY_ACCOUNTS_ID, accountType, noteType, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Tests the InitBinder for Note ")
    void testInitDataBinderSuccess() {
        AccountTypeConverter converter = new AccountTypeConverter();

        AccountType type = AccountType.SMALL_FULL;
        converter.setAsText("small-full");
        WebDataBinder binder = new WebDataBinder(type, converter.getAsText());
        noteController.initBinder(binder);
        assertNotNull(binder);
        assertEquals(type.toString(), converter.getAsText());
    }
}