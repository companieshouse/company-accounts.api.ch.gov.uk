package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import com.mongodb.MongoException;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RptTransactionDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RptTransactionEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransaction;
import uk.gov.companieshouse.api.accounts.repository.smallfull.RptTransactionRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.RptTransactionTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RptTransactionServiceImplTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "selfLink";
    private static final String URI = "/transactions/transactionId/company-accounts/" +
            COMPANY_ACCOUNTS_ID + "/small-full/notes/related-party-transactions/transactions/" +
            GENERATED_ID;
    private static final String RPT_TRANSACTION_LINK = SELF_LINK + "/company-accounts/" +
            COMPANY_ACCOUNTS_ID + "/small-full/notes/related-party-transactions/transactions";


    @Mock
    private RptTransactionTransformer transformer;

    @Mock
    private RptTransactionRepository repository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private RptTransaction rptTransaction;

    @Mock
    private Transaction transaction;

    @Mock
    private RptTransactionEntity rptTransactionEntity;

    @Mock
    private RptTransactionDataEntity rptTransactionDataEntity;

    @Mock
    private Map<String, String> links;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private RelatedPartyTransactionsServiceImpl relatedPartyTransactionImpl;

    @InjectMocks
    private RptTransactionServiceImpl service;

    @Test
    @DisplayName("Tests successful creation of a related party transactions transaction resource")
    void createRptTransactionSuccess() throws DataException {

        when(transformer.transform(rptTransaction)).thenReturn(rptTransactionEntity);
        when(keyIdGenerator.generateRandom()).thenReturn(GENERATED_ID);

        when(rptTransaction.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(SELF_LINK);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);
        doNothing().when(relatedPartyTransactionImpl).addRptTransaction(COMPANY_ACCOUNTS_ID, GENERATED_ID, SELF_LINK, request);

        ResponseObject<RptTransaction> response =
                service.create(rptTransaction, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(rptTransaction, response.getData());

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
    }

    @Test
    @DisplayName("Tests the creation of a related party transactions transaction resource where the repository throws a duplicate key exception")
    void createRptTransactionDuplicateKeyException() throws DataException {

        when(transformer.transform(rptTransaction)).thenReturn(rptTransactionEntity);
        when(repository.insert(rptTransactionEntity)).thenThrow(DuplicateKeyException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<RptTransaction> response =
                service.create(rptTransaction, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a related party transactions transaction resource where the repository throws a mongo exception")
    void createRptTransactionMongoException() throws DataException {

        when(transformer.transform(rptTransaction)).thenReturn(rptTransactionEntity);
        when(repository.insert(rptTransactionEntity)).thenThrow(MongoException.class);
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class, () ->
                service.create(rptTransaction, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful retrieval of a related party transactions transaction resource")
    void getRptTransactionSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(rptTransactionEntity));
        when(transformer.transform(rptTransactionEntity)).thenReturn(rptTransaction);

        ResponseObject<RptTransaction> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(rptTransaction, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent related party transactions transaction resource")
    void getRptTransactionNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        ResponseObject<RptTransaction> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a related party transactions transaction resource where the repository throws a MongoException")
    void getRptTransactionThrowsMongoException() {

        when(request.getRequestURI()).thenReturn(URI);
        when(repository.findById(GENERATED_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful retrieval of all transactions for a related party transactions resource")
    void getAllRptTransactionsSuccess() throws DataException {

        RptTransactionEntity[] rptTransactionEntities = new RptTransactionEntity[]{rptTransactionEntity};
        RptTransaction[] rptTransactions = new RptTransaction[]{rptTransaction};

        when(repository.findAllTransactions(RPT_TRANSACTION_LINK)).thenReturn(rptTransactionEntities);
        when(transformer.transform(rptTransactionEntities)).thenReturn(rptTransactions);
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<RptTransaction> response =
                service.findAll(transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(rptTransactions, response.getDataForMultipleResources());
    }

    @Test
    @DisplayName("Tests the retrieval of non-existent transactions for a related party transactions resource")
    void getAllRptTransactionsNotFound() throws DataException {

        RptTransactionEntity[] rptTransactionEntities = new RptTransactionEntity[]{};
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);
        when(repository.findAllTransactions(RPT_TRANSACTION_LINK)).thenReturn(rptTransactionEntities);

        ResponseObject<RptTransaction> response =
                service.findAll(transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of all transactions for a related party transactions resource where the repository throws a MongoException")
    void getAllRptTransactionsThrowsMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);
        when(repository.findAllTransactions(RPT_TRANSACTION_LINK)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.findAll(transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests successful update of a related party transactions transaction resource")
    void updateRptTransactionSuccess() throws DataException {

        when(transformer.transform(rptTransaction)).thenReturn(rptTransactionEntity);
        when(request.getRequestURI()).thenReturn(URI);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<RptTransaction> response =
                service.update(rptTransaction, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(rptTransaction, response.getData());

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
    }

    @Test
    @DisplayName("Tests the update of a related party transactions transaction resource where the repository throws a mongo exception")
    void updateRptTransactionMongoException() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);
        when(transformer.transform(rptTransaction)).thenReturn(rptTransactionEntity);
        when(repository.save(rptTransactionEntity)).thenThrow(MongoException.class);
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class, () ->
                service.update(rptTransaction, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful deletion of a related party transactions transaction resource")
    void deleteRptTransactionSuccess() throws DataException {
        
        when(request.getRequestURI()).thenReturn(URI);
        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<RptTransaction> response =
                service.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a non existent related party transactions transaction resource")
    void deleteRptTransactionNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);
        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<RptTransaction> response =
                service.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a related party transactions transaction resource where the repository throws a MongoException")
    void deleteRptTransactionThrowsMongoException() {

        when(request.getRequestURI()).thenReturn(URI);
        when(repository.existsById(GENERATED_ID)).thenReturn(true);
        
        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                service.delete(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful deletion of all transactions for a related party transactions resource")
    void deleteAllRptTransactionsSuccess() throws DataException {
        
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);
        doNothing().when(repository).deleteAllTransactions(RPT_TRANSACTION_LINK);

        ResponseObject<RptTransaction> response =
                service.deleteAll(transaction, COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteAllTransactionsCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of all non existent transactions for a related party transactions resource")
    void deleteAllRptTransactionsNotFound() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<RptTransaction> response =
                service.deleteAll(transaction, COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of all transactions for a related party transactions resource where the repository throws a MongoException")
    void deleteAllRptTransactionsThrowsMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);
        
        doThrow(MongoException.class).when(repository).deleteAllTransactions(RPT_TRANSACTION_LINK);

        assertThrows(DataException.class, () ->
                service.deleteAll(transaction, COMPANY_ACCOUNTS_ID, request));
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(rptTransactionEntity).setId(GENERATED_ID);
    }

    private void assertMetaDataSetOnRestObject() {
        verify(rptTransaction).setKind(anyString());
        verify(rptTransaction).setEtag(anyString());
        verify(rptTransaction).setLinks(anyMap());
    }

    private void assertRepositoryInsertCalled() {
        verify(repository).insert(rptTransactionEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository).save(rptTransactionEntity);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository).deleteById(GENERATED_ID);
    }

    private void assertRepositoryDeleteAllTransactionsCalled() {
        verify(repository).deleteAllTransactions(RPT_TRANSACTION_LINK);
    }
}
