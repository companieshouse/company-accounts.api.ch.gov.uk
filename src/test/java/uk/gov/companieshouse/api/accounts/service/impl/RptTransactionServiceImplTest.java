package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
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
import uk.gov.companieshouse.api.accounts.ResourceName;
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

    @Mock
    private RptTransactionTransformer transformer;

    @Mock
    private RptTransactionRepository repository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private RptTransaction rptTransactions;

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

    @InjectMocks
    private RptTransactionServiceImpl service;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "selfLink";

    @Test
    @DisplayName("Tests successful creation of a related party transactions transaction resource")
    void createRptTransactionSuccess() throws DataException {

        when(transformer.transform(rptTransactions)).thenReturn(rptTransactionEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RPT_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(rptTransactions.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(SELF_LINK);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<RptTransaction> response =
                service.create(rptTransactions, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(rptTransactions, response.getData());

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
    }

    @Test
    @DisplayName("Tests the creation of a related party transactions transaction resource where the repository throws a duplicate key exception")
    void createRptTransactionDuplicateKeyException() throws DataException {

        when(transformer.transform(rptTransactions)).thenReturn(rptTransactionEntity);
//        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RPT_TRANSACTIONS.getName()))
//                .thenReturn(GENERATED_ID);
        when(repository.insert(rptTransactionEntity)).thenThrow(DuplicateKeyException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<RptTransaction> response =
                service.create(rptTransactions, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a related party transactions transaction resource where the repository throws a mongo exception")
    void createRptTransactionMongoException() throws DataException {

        when(transformer.transform(rptTransactions)).thenReturn(rptTransactionEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RPT_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(rptTransactionEntity)).thenThrow(MongoException.class);
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class, () ->
                service.create(rptTransactions, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful retrieval of a related party transactions transaction resource")
    void getRptTransactionSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RPT_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(rptTransactionEntity));
        when(transformer.transform(rptTransactionEntity)).thenReturn(rptTransactions);

        ResponseObject<RptTransaction> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(rptTransactions, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent related party transactions transaction resource")
    void getRptTransactionNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RPT_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        ResponseObject<RptTransaction> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a related party transactions transaction resource where the repository throws a MongoException")
    void getRptTransactionThrowsMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RPT_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful deletion of a related party transactions transaction resource")
    void deleteRptTransactionSuccess() throws DataException {
        
//        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RPT_TRANSACTIONS.getName()))
//                .thenReturn(GENERATED_ID);

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

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RPT_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

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

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RPT_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                service.delete(COMPANY_ACCOUNTS_ID, request));
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(rptTransactionEntity).setId(GENERATED_ID);
    }

    private void assertMetaDataSetOnRestObject() {
        verify(rptTransactions).setKind(anyString());
        verify(rptTransactions).setEtag(anyString());
        verify(rptTransactions).setLinks(anyMap());
    }

    private void assertRepositoryInsertCalled() {
        verify(repository).insert(rptTransactionEntity);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository).deleteById(GENERATED_ID);
    }
}
