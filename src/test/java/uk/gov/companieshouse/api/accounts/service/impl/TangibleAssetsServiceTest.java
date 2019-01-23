package uk.gov.companieshouse.api.accounts.service.impl;

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

import com.mongodb.MongoException;
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
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.TangibleAssetsRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.TangibleAssetsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.TangibleAssetsValidator;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TangibleAssetsServiceTest {

    @Mock
    private TangibleAssetsTransformer transformer;

    @Mock
    private TangibleAssetsRepository repository;

    @Mock
    private TangibleAssetsValidator validator;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private TangibleAssets tangibleAssets;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TangibleAssetsEntity tangibleAssetsEntity;

    @Mock
    private Map<String, String> links;

    @Mock
    private Errors errors;

    @InjectMocks
    private TangibleAssetsService tangibleAssetsService;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "selfLink";

    @Test
    @DisplayName("Tests the successful creation of a Tangible Assets resource")
    void createTangibleAssetsSuccess() throws DataException {

        when(validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transformer.transform(tangibleAssets)).thenReturn(tangibleAssetsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.TANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(tangibleAssets.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(SELF_LINK);

        ResponseObject<TangibleAssets> response =
                tangibleAssetsService.create(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(tangibleAssets, response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a Tangible Assets resource where the repository throws a duplicate key exception")
    void createTangibleAssetsDuplicateKeyException() throws DataException {

        when(validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transformer.transform(tangibleAssets)).thenReturn(tangibleAssetsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.TANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(tangibleAssetsEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<TangibleAssets> response =
                tangibleAssetsService.create(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a Tangible Assets resource where the repository throws a Mongo exception")
    void createTangibleAssetsMongoException() throws DataException {

        when(validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transformer.transform(tangibleAssets)).thenReturn(tangibleAssetsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.TANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(tangibleAssetsEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                tangibleAssetsService.create(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the creation of a Tangible Assets resource where the validation errors are present")
    void createTangibleAssetsWithValidationErrors() throws DataException {

        when(validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(true);

        ResponseObject<TangibleAssets> response =
                tangibleAssetsService.create(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.VALIDATION_ERROR, response.getStatus());
        verify(repository, never()).insert(any(TangibleAssetsEntity.class));
        assertWhetherSmallFullServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the successful update of a Tangible Assets resource")
    void updateTangibleAssetsSuccess() throws DataException {

        when(validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transformer.transform(tangibleAssets)).thenReturn(tangibleAssetsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.TANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        ResponseObject<TangibleAssets> response =
                tangibleAssetsService.update(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(tangibleAssets, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a Tangible Assets resource where the repository throws a Mongo exception")
    void updateTangibleAssetsMongoException() throws DataException {

        when(validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transformer.transform(tangibleAssets)).thenReturn(tangibleAssetsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.TANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.save(tangibleAssetsEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                tangibleAssetsService.update(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
    }

    @Test
    @DisplayName("Tests the update of a Tangible Assets resource where the validation errors are present")
    void updateTangibleAssetsWithValidationErrors() throws DataException {

        when(validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(true);

        ResponseObject<TangibleAssets> response =
                tangibleAssetsService.update(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.VALIDATION_ERROR, response.getStatus());
        verify(repository, never()).insert(any(TangibleAssetsEntity.class));
    }

    @Test
    @DisplayName("Tests the successful retrieval of a Tangible Assets resource")
    void getTangibleAssetsSuccess() throws DataException {

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(tangibleAssetsEntity));
        when(transformer.transform(tangibleAssetsEntity)).thenReturn(tangibleAssets);

        ResponseObject<TangibleAssets> response =
                tangibleAssetsService.findById(GENERATED_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(tangibleAssets, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent Tangible Assets resource")
    void getTangibleAssetsNotFound() throws DataException {

        TangibleAssetsEntity tangibleAssetsEntity = null;
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.ofNullable(tangibleAssetsEntity));

        ResponseObject<TangibleAssets> response =
                tangibleAssetsService.findById(GENERATED_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a Tangible Assets resource where the repository throws a Mongo exception")
    void getTangibleAssetsMongoException() {

        when(repository.findById(GENERATED_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                tangibleAssetsService.findById(GENERATED_ID, request));

        assertRepositoryFindByIdCalled();
    }

    @Test
    @DisplayName("Tests the successful deletion of a Tangible Assets resource")
    void deleteTangibleAssetsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.TANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<TangibleAssets> response =
                tangibleAssetsService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a Tangible Assets resource where the repository throws a Mongo exception")
    void deleteTangibleAssetsMongoException() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.TANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                tangibleAssetsService.delete(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent Tangible Assets resource")
    void deleteTangibleAssetsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.TANGIBLE_ASSETS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<TangibleAssets> response =
                tangibleAssetsService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertWhetherSmallFullServiceCalledToRemoveLink(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    private void assertMetaDataSetOnRestObject() {
        verify(tangibleAssets, times(1)).setKind(anyString());
        verify(tangibleAssets, times(1)).setEtag(anyString());
        verify(tangibleAssets, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(tangibleAssetsEntity, times(1)).setId(GENERATED_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(tangibleAssetsEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(tangibleAssetsEntity);
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
                .addLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.TANGIBLE_ASSETS_NOTE, SELF_LINK, request);
    }

    private void assertWhetherSmallFullServiceCalledToRemoveLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(smallFullService, timesExpected)
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.TANGIBLE_ASSETS_NOTE, request);
    }

}
