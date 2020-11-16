package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.HashMap;
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
import org.mockito.verification.VerificationMode;
import org.springframework.dao.DuplicateKeyException;
import com.mongodb.MongoException;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.RelatedPartyTransactionsLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RelatedPartyTransactionsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RelatedPartyTransactionsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RelatedPartyTransactions;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransaction;
import uk.gov.companieshouse.api.accounts.repository.smallfull.RelatedPartyTransactionsRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.RelatedPartyTransactionsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RelatedPartyTransactionsServiceImplTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "selfLink";
    private static final String RPT_TRANSACTION_ID = "rptTransactionId";
    private static final String RPT_TRANSACTION_SELF_LINK = "rptTransaction/selfLink";

    @Mock
    private RelatedPartyTransactionsTransformer transformer;

    @Mock
    private RelatedPartyTransactionsRepository repository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private RelatedPartyTransactions relatedPartyTransactions;

    @Mock
    private Transaction transaction;

    @Mock
    private RelatedPartyTransactionsEntity relatedPartyTransactionsEntity;

    @Mock
    private RelatedPartyTransactionsDataEntity relatedPartyTransactionsDataEntity;

    @Mock
    private Map<String, String> links;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private RptTransaction rptTransaction;

    @InjectMocks
    private RelatedPartyTransactionsServiceImpl service;

    @Test
    @DisplayName("Tests successful creation of a related party transactions resource")
    void createRelatedPartyTransactionsSuccess() throws DataException {

        when(transformer.transform(relatedPartyTransactions)).thenReturn(relatedPartyTransactionsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(relatedPartyTransactions.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(SELF_LINK);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<RelatedPartyTransactions> response =
                service.create(relatedPartyTransactions, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(relatedPartyTransactions, response.getData());

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(true);
    }

    @Test
    @DisplayName("Tests the creation of a related party transactions resource where the repository throws a duplicate key exception")
    void createRelatedPartyTransactionsDuplicateKeyException() throws DataException {

        when(transformer.transform(relatedPartyTransactions)).thenReturn(relatedPartyTransactionsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(relatedPartyTransactionsEntity)).thenThrow(DuplicateKeyException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<RelatedPartyTransactions> response =
                service.create(relatedPartyTransactions, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());

        assertWhetherSmallFullServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the creation of a related party transactions resource where the repository throws a mongo exception")
    void createRelatedPartyTransactionsMongoException() throws DataException {

        when(transformer.transform(relatedPartyTransactions)).thenReturn(relatedPartyTransactionsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(relatedPartyTransactionsEntity)).thenThrow(MongoException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class, () ->
                service.create(relatedPartyTransactions, transaction, COMPANY_ACCOUNTS_ID, request));

        assertWhetherSmallFullServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the successful retrieval of a related party transactions resource")
    void getRelatedPartyTransactionsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(relatedPartyTransactionsEntity));
        when(transformer.transform(relatedPartyTransactionsEntity)).thenReturn(relatedPartyTransactions);

        ResponseObject<RelatedPartyTransactions> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(relatedPartyTransactions, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent related party transactions resource")
    void getRelatedPartyTransactionsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        ResponseObject<RelatedPartyTransactions> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a related party transactions resource where the repository throws a MongoException")
    void getRelatedPartyTransactionsThrowsMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful deletion of a related party transactions resource")
    void deleteRelatedPartyTransactionsSuccess() throws DataException {
        
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<RelatedPartyTransactions> response =
                service.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a non existent related party transactions resource")
    void deleteRelatedPartyTransactionsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<RelatedPartyTransactions> response =
                service.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertWhetherSmallFullServiceCalledToRemoveLink(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a related party transactions resource where the repository throws a MongoException")
    void deleteRelatedPartyTransactionsThrowsMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                service.delete(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful addition of a related party transactions resource link")
    void addLinkSuccess() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(relatedPartyTransactionsEntity));

        when(relatedPartyTransactionsEntity.getData()).thenReturn(relatedPartyTransactionsDataEntity);
        when(relatedPartyTransactionsDataEntity.getLinks()).thenReturn(links);

        RelatedPartyTransactionsLinkType relatedPartyTransactionsLinkType = RelatedPartyTransactionsLinkType.SELF;

        assertAll(() -> service.addLink(COMPANY_ACCOUNTS_ID, relatedPartyTransactionsLinkType, SELF_LINK, request));

        verify(links).put(relatedPartyTransactionsLinkType.getLink(), SELF_LINK);
        verify(repository).save(relatedPartyTransactionsEntity);
    }

    @Test
    @DisplayName("Tests the addition of a related party transactions resource link where the repository throws a Mongo exception")
    void addLinkMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(relatedPartyTransactionsEntity));

        when(relatedPartyTransactionsEntity.getData()).thenReturn(relatedPartyTransactionsDataEntity);
        when(relatedPartyTransactionsDataEntity.getLinks()).thenReturn(links);

        when(repository.save(relatedPartyTransactionsEntity)).thenThrow(MongoException.class);

        RelatedPartyTransactionsLinkType relatedPartyTransactionsLinkType = RelatedPartyTransactionsLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.addLink(COMPANY_ACCOUNTS_ID, relatedPartyTransactionsLinkType, SELF_LINK, request));

        verify(links).put(relatedPartyTransactionsLinkType.getLink(), SELF_LINK);
    }

    @Test
    @DisplayName("Tests the addition of a related party transactions resource link where the entity is not found")
    void addLinkRelatedPartyTransactionsEntityNotFound() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        RelatedPartyTransactionsLinkType relatedPartyTransactionsLinkType = RelatedPartyTransactionsLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.addLink(COMPANY_ACCOUNTS_ID, relatedPartyTransactionsLinkType, SELF_LINK, request));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Tests the successful removal of a related party transactions resource link")
    void removeLinkSuccess() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(relatedPartyTransactionsEntity));

        when(relatedPartyTransactionsEntity.getData()).thenReturn(relatedPartyTransactionsDataEntity);
        when(relatedPartyTransactionsDataEntity.getLinks()).thenReturn(links);

        RelatedPartyTransactionsLinkType relatedPartyTransactionsLinkType = RelatedPartyTransactionsLinkType.SELF;

        assertAll(() -> service.removeLink(COMPANY_ACCOUNTS_ID, relatedPartyTransactionsLinkType, request));

        verify(links).remove(relatedPartyTransactionsLinkType.getLink());
        verify(repository).save(relatedPartyTransactionsEntity);
    }

    @Test
    @DisplayName("Tests the removal of a related party transactions resource link where the repository throws a Mongo exception")
    void removeLinkMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(relatedPartyTransactionsEntity));

        when(relatedPartyTransactionsEntity.getData()).thenReturn(relatedPartyTransactionsDataEntity);
        when(relatedPartyTransactionsDataEntity.getLinks()).thenReturn(links);

        when(repository.save(relatedPartyTransactionsEntity)).thenThrow(MongoException.class);

        RelatedPartyTransactionsLinkType relatedPartyTransactionsLinkType = RelatedPartyTransactionsLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.removeLink(COMPANY_ACCOUNTS_ID, relatedPartyTransactionsLinkType, request));

        verify(links).remove(relatedPartyTransactionsLinkType.getLink());
    }

    @Test
    @DisplayName("Tests the removal of a related party transactions resource link where the entity is not found")
    void removeLinkRelatedPartyTransactionsEntityNotFound() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        RelatedPartyTransactionsLinkType relatedPartyTransactionsLinkType = RelatedPartyTransactionsLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.removeLink(COMPANY_ACCOUNTS_ID, relatedPartyTransactionsLinkType, request));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Tests successful additon of a RPT transaction to a related party transactions resource")
    void addRptTransactionToRelatedPartyTransactionResourceSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(relatedPartyTransactionsEntity));
        when(relatedPartyTransactionsEntity.getData()).thenReturn(relatedPartyTransactionsDataEntity);
        when(relatedPartyTransactionsDataEntity.getTransactions()).thenReturn(new HashMap<>());

        assertAll(() -> service.addRptTransaction(COMPANY_ACCOUNTS_ID, RPT_TRANSACTION_ID, RPT_TRANSACTION_SELF_LINK, request));

        assertRepositorySaveCalled();
    }

    @Test
    @DisplayName("Tests additon of a RPT transaction to a related party transactions resource where entity is not found")
    void addRptTransactionToRelatedPartyTransactionEntityNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        assertThrows(DataException.class,
                () -> service.addRptTransaction(COMPANY_ACCOUNTS_ID, RPT_TRANSACTION_ID, RPT_TRANSACTION_SELF_LINK, request));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Tests successful removal of a RPT transaction to a related party transactions resource")
    void removeRptTransactionFromRelatedPartyTransactionResourceSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(relatedPartyTransactionsEntity));
        when(relatedPartyTransactionsEntity.getData()).thenReturn(relatedPartyTransactionsDataEntity);

        assertAll(() -> service.removeAllRptTransactions(COMPANY_ACCOUNTS_ID));

        assertRepositorySaveCalled();
    }

    @Test
    @DisplayName("Tests removal of a RPT transaction to a related party transactions resource where entity is not found")
    void removeRptTransactionToRelatedPartyTransactionEntityNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        assertThrows(DataException.class,
                () -> service.addRptTransaction(COMPANY_ACCOUNTS_ID, RPT_TRANSACTION_ID, RPT_TRANSACTION_SELF_LINK, request));

        verify(repository, never()).save(any());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(relatedPartyTransactionsEntity).setId(GENERATED_ID);
    }

    private void assertMetaDataSetOnRestObject() {
        verify(relatedPartyTransactions).setKind(anyString());
        verify(relatedPartyTransactions).setEtag(anyString());
        verify(relatedPartyTransactions).setLinks(anyMap());
    }

    private void assertRepositoryInsertCalled() {
        verify(repository).insert(relatedPartyTransactionsEntity);
    }

    private void assertRepositorySaveCalled() {
        verify(repository).save(relatedPartyTransactionsEntity);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository).deleteById(GENERATED_ID);
    }

    private void assertWhetherSmallFullServiceCalledToAddLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(smallFullService, timesExpected)
                .addLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.RELATED_PARTY_TRANSACTIONS, SELF_LINK, request);
    }

    private void assertWhetherSmallFullServiceCalledToRemoveLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(smallFullService, timesExpected)
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.RELATED_PARTY_TRANSACTIONS, request);
    }
}
