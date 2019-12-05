package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.DirectorsReportLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.StatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Statements;
import uk.gov.companieshouse.api.accounts.repository.StatementsRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.StatementsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class )
public class StatementsServiceTest {

    @Mock
    private StatementsTransformer transformer;

    @Mock
    private StatementsRepository repository;

    @Mock
    private DirectorsReportServiceImpl directorsReportService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private Statements statements;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private HttpServletRequest request;

    @Mock
    private StatementsEntity statementsEntity;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private StatementsService statementsService;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String STATEMENTS_ID = "statementsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String STATEMENTS_SELF_LINK = "statementsSelfLink";


    @Test
    @DisplayName("Tests the successful creation of a statements resource")
    void createStatementsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(transformer.transform(statements)).thenReturn(statementsEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(statements.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(STATEMENTS_SELF_LINK);

        ResponseObject<Statements> response =
                statementsService.create(statements, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddLink(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(statements, response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a statements resource where the repository throws a duplicate key exception")
    void createStatementsDuplicateKeyException() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(transformer.transform(statements)).thenReturn(statementsEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(statementsEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<Statements> response =
                statementsService.create(statements, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddLink(false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a statements resource where the repository throws a Mongo exception")
    void createStatementsMongoException() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(transformer.transform(statements)).thenReturn(statementsEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(statementsEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                statementsService.create(statements, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the successful update of a statements resource")
    void updateStatementsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(transformer.transform(statements)).thenReturn(statementsEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        ResponseObject<Statements> response =
                statementsService.update(statements, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(statements, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a statements resource where the repository throws a Mongo exception")
    void updateStatementsMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(transformer.transform(statements)).thenReturn(statementsEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.save(statementsEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                statementsService.update(statements, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
    }

    @Test
    @DisplayName("Tests the successful retrieval of a statements resource")
    void getStatementsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(statementsEntity));
        when(transformer.transform(statementsEntity)).thenReturn(statements);

        ResponseObject<Statements> response =
                statementsService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(statements, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent statements resource")
    void getStatementsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        StatementsEntity statementsEntity = null;
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.ofNullable(statementsEntity));

        ResponseObject<Statements> response =
                statementsService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a statements resource where the repository throws a Mongo exception")
    void getStatementsMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                statementsService.find(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryFindByIdCalled();
    }

    @Test
    @DisplayName("Tests the successful deletion of a statements resource")
    void deleteStatementsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<Statements> response =
                statementsService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveLink(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a statements resource where the repository throws a Mongo exception")
    void deleteStatementsMongoException() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                statementsService.delete(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveLink(false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent statements resource")
    void deleteStatementsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STATEMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<Statements> response =
                statementsService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertWhetherDirectorsReportServiceCalledToRemoveLink(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    private void assertMetaDataSetOnRestObject() {
        verify(statements, times(1)).setKind(Kind.DIRECTORS_REPORT_STATEMENTS.getValue());
        verify(statements, times(1)).setEtag(anyString());
        verify(statements, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(statementsEntity, times(1)).setId(GENERATED_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(statementsEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(statementsEntity);
    }

    private void assertRepositoryFindByIdCalled() {
        verify(repository, times(1)).findById(GENERATED_ID);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository, times(1)).deleteById(GENERATED_ID);
    }

    private void assertWhetherDirectorsReportServiceCalledToAddLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(directorsReportService, timesExpected)
                .addLink(COMPANY_ACCOUNTS_ID, DirectorsReportLinkType.STATEMENTS, STATEMENTS_SELF_LINK, request);
    }

    private void assertWhetherDirectorsReportServiceCalledToRemoveLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(directorsReportService, timesExpected)
                .removeLink(COMPANY_ACCOUNTS_ID, DirectorsReportLinkType.STATEMENTS, request);
    }

} 
