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
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.ParentService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.Validator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseServiceTest {

    @Mock
    private GenericTransformer<RestObject, BaseEntity> transformer;

    @Mock
    private MongoRepository<BaseEntity, String> repository;

    @Mock
    private Validator<RestObject> validator;

    @Mock
    private ParentService<?, LinkType> parentService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private LinkType linkType;

    private Kind kind = Kind.COMPANY_ACCOUNTS;

    private ResourceName resourceName = ResourceName.COMPANY_ACCOUNT;

    @Mock
    private RestObject rest;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private BaseEntity entity;

    @Mock
    private Errors errors;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private BaseService<RestObject, BaseEntity, LinkType> baseService
            = new BaseService<>(repository, transformer, validator, keyIdGenerator, parentService, linkType, kind, resourceName);

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "selfLink";

    @Test
    @DisplayName("Tests the successful creation of a resource")
    void createSuccess() throws DataException {

        when(validator.validateSubmission(rest, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transformer.transform(rest)).thenReturn(entity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);

        when(rest.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(SELF_LINK);

        ResponseObject<RestObject> response =
                baseService.create(rest, transaction, COMPANY_ACCOUNTS_ID, request, SELF_LINK);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherParentServiceCalledToAddLink(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(rest, response.getData());
    }

    @Test
    @DisplayName("Tests the successful creation of a resource without performing any validation")
    void createSuccessWithoutValidation() throws DataException {

        ReflectionTestUtils.setField(baseService, "validator", null);

        when(transformer.transform(rest)).thenReturn(entity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);

        when(rest.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(SELF_LINK);

        ResponseObject<RestObject> response =
                baseService.create(rest, transaction, COMPANY_ACCOUNTS_ID, request, SELF_LINK);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherParentServiceCalledToAddLink(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(rest, response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a resource where the repository throws a duplicate key exception")
    void createDuplicateKeyException() throws DataException {

        when(validator.validateSubmission(rest, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transformer.transform(rest)).thenReturn(entity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(entity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<RestObject> response =
                baseService.create(rest, transaction, COMPANY_ACCOUNTS_ID, request, SELF_LINK);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherParentServiceCalledToAddLink(false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a resource where the repository throws a Mongo exception")
    void createMongoException() throws DataException {

        when(validator.validateSubmission(rest, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transformer.transform(rest)).thenReturn(entity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(entity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                baseService.create(rest, transaction, COMPANY_ACCOUNTS_ID, request, SELF_LINK));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherParentServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the creation of a resource where validation errors are present")
    void createWithValidationErrors() throws DataException {

        when(validator.validateSubmission(rest, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(true);

        ResponseObject<RestObject> response =
                baseService.create(rest, transaction, COMPANY_ACCOUNTS_ID, request, SELF_LINK);

        assertEquals(ResponseStatus.VALIDATION_ERROR, response.getStatus());
        verify(repository, never()).insert(any(BaseEntity.class));
        assertWhetherParentServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the successful update of a resource")
    void updateSuccess() throws DataException {

        when(validator.validateSubmission(rest, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transformer.transform(rest)).thenReturn(entity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);

        ResponseObject<RestObject> response =
                baseService.update(rest, transaction, COMPANY_ACCOUNTS_ID, request, SELF_LINK);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(rest, response.getData());
    }

    @Test
    @DisplayName("Tests the successful update of a resource without performing any validation")
    void updateSuccessWithoutValidation() throws DataException {

        ReflectionTestUtils.setField(baseService, "validator", null);

        when(transformer.transform(rest)).thenReturn(entity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);

        ResponseObject<RestObject> response =
                baseService.update(rest, transaction, COMPANY_ACCOUNTS_ID, request, SELF_LINK);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(rest, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a resource where the repository throws a Mongo exception")
    void updateMongoException() throws DataException {

        when(validator.validateSubmission(rest, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transformer.transform(rest)).thenReturn(entity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.save(entity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                baseService.update(rest, transaction, COMPANY_ACCOUNTS_ID, request, SELF_LINK));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
    }

    @Test
    @DisplayName("Tests the update of a resource where validation errors are present")
    void updateWithValidationErrors() throws DataException {

        when(validator.validateSubmission(rest, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(true);

        ResponseObject<RestObject> response =
                baseService.update(rest, transaction, COMPANY_ACCOUNTS_ID, request, SELF_LINK);

        assertEquals(ResponseStatus.VALIDATION_ERROR, response.getStatus());
        verify(repository, never()).save(any(BaseEntity.class));
    }

    @Test
    @DisplayName("Tests the successful retrieval of a resource")
    void getSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(entity));
        when(transformer.transform(entity)).thenReturn(rest);

        ResponseObject<RestObject> response =
                baseService.find(COMPANY_ACCOUNTS_ID);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(rest, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent resource")
    void getNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);

        BaseEntity entity = null;
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.ofNullable(entity));

        ResponseObject<RestObject> response =
                baseService.find(COMPANY_ACCOUNTS_ID);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a resource where the repository throws a Mongo exception")
    void getMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                baseService.find(COMPANY_ACCOUNTS_ID));

        assertRepositoryFindByIdCalled();
    }

    @Test
    @DisplayName("Tests the successful deletion of a resource")
    void deleteSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<RestObject> response =
                baseService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherParentServiceCalledToRemoveLink(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a resource where the repository throws a Mongo exception")
    void deleteMongoException() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                baseService.delete(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryDeleteByIdCalled();
        assertWhetherParentServiceCalledToRemoveLink(false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent resource")
    void deleteNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + resourceName.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<RestObject> response =
                baseService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertWhetherParentServiceCalledToRemoveLink(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    private void assertMetaDataSetOnRestObject() {
        verify(rest, times(1)).setKind(kind.getValue());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(entity, times(1)).setId(GENERATED_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(entity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(entity);
    }

    private void assertRepositoryFindByIdCalled() {
        verify(repository, times(1)).findById(GENERATED_ID);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository, times(1)).deleteById(GENERATED_ID);
    }

    private void assertWhetherParentServiceCalledToAddLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(parentService, timesExpected)
                .addLink(COMPANY_ACCOUNTS_ID, linkType, SELF_LINK, request);
    }

    private void assertWhetherParentServiceCalledToRemoveLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(parentService, timesExpected)
                .removeLink(COMPANY_ACCOUNTS_ID, linkType, request);
    }
}
