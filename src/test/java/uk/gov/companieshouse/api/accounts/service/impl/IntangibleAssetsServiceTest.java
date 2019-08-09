package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.intangible.IntangibleAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.repository.IntangibleAssetsRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.IntangibleAssetsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntangibleAssetsServiceTest {

    @Mock
    private IntangibleAssetsTransformer transformer;

    @Mock
    private IntangibleAssetsRepository repository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private IntangibleAssets intangibleAssets;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private HttpServletRequest request;

    @Mock
    private IntangibleAssetsEntity intangibleAssetsEntity;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private IntangibleAssetsService intangibleAssetsService;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "selfLink";

    @Test
    @DisplayName("Tests the successful creation of a Intangible Assets resource")
    void createIntangibleAssetsSuccess() throws DataException {


        when(transformer.transform(intangibleAssets)).thenReturn(intangibleAssetsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(intangibleAssets.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(SELF_LINK);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<IntangibleAssets> response =
                intangibleAssetsService.create(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(intangibleAssets, response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a Intangible Assets resource where the repository throws a duplicate key exception")
    void createIntangibleAssetsDuplicateKeyException() throws DataException {

        when(transformer.transform(intangibleAssets)).thenReturn(intangibleAssetsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(intangibleAssetsEntity)).thenThrow(DuplicateKeyException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<IntangibleAssets> response =
                intangibleAssetsService.create(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a Intangible Assets resource where the repository throws a Mongo exception")
    void createIntangibleAssetsMongoException() throws DataException {

        when(transformer.transform(intangibleAssets)).thenReturn(intangibleAssetsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(intangibleAssetsEntity)).thenThrow(MongoException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class, () ->
                intangibleAssetsService.create(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the successful update of a Intangible Assets resource")
    void updateIntangibleAssetsSuccess() throws DataException {

        when(transformer.transform(intangibleAssets)).thenReturn(intangibleAssetsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<IntangibleAssets> response =
                intangibleAssetsService.update(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(intangibleAssets, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a Intangible Assets resource where the repository throws a Mongo exception")
    void updateIntangibleAssetsMongoException() throws DataException {

        when(transformer.transform(intangibleAssets)).thenReturn(intangibleAssetsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.save(intangibleAssetsEntity)).thenThrow(MongoException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class, () ->
                intangibleAssetsService.update(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
    }

    @Test
    @DisplayName("Tests the successful retrieval of a Intangible Assets resource")
    void getIntangibleAssetsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(intangibleAssetsEntity));
        when(transformer.transform(intangibleAssetsEntity)).thenReturn(intangibleAssets);

        ResponseObject<IntangibleAssets> response =
                intangibleAssetsService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(intangibleAssets, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent Intangible Assets resource")
    void getIntangibleAssetsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        IntangibleAssetsEntity intangibleAssetsEntity = null;
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.ofNullable(intangibleAssetsEntity));

        ResponseObject<IntangibleAssets> response =
                intangibleAssetsService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a Intangible Assets resource where the repository throws a Mongo exception")
    void getIntangibleAssetsMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                intangibleAssetsService.find(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryFindByIdCalled();
    }

    @Test
    @DisplayName("Tests the successful deletion of a Intangible Assets resource")
    void deleteIntangibleAssetsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<IntangibleAssets> response =
                intangibleAssetsService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a Intangible Assets resource where the repository throws a Mongo exception")
    void deleteIntangibleAssetsMongoException() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                intangibleAssetsService.delete(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent Intangible Assets resource")
    void deleteIntangibleAssetsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.INTANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<IntangibleAssets> response =
                intangibleAssetsService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertWhetherSmallFullServiceCalledToRemoveLink(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    private void assertMetaDataSetOnRestObject() {
        verify(intangibleAssets, times(1)).setKind(anyString());
        verify(intangibleAssets, times(1)).setEtag(anyString());
        verify(intangibleAssets, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(intangibleAssetsEntity, times(1)).setId(GENERATED_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(intangibleAssetsEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(intangibleAssetsEntity);
    }

    private void assertRepositoryFindByIdCalled() {
        verify(repository, times(1)).findById(GENERATED_ID);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository, times(1)).deleteById(GENERATED_ID);
    }

    private void assertWhetherSmallFullServiceCalledToAddLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(smallFullService, timesExpected)
                .addLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.INTANGIBLE_ASSETS_NOTE, SELF_LINK, request);
    }

    private void assertWhetherSmallFullServiceCalledToRemoveLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(smallFullService, timesExpected)
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.INTANGIBLE_ASSETS_NOTE, request);
    }

}
