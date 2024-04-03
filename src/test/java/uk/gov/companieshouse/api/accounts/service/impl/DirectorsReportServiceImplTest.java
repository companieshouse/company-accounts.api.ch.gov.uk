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
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.DirectorsReportLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;
import uk.gov.companieshouse.api.accounts.repository.DirectorsReportRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.DirectorsReportTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
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

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorsReportServiceImplTest {
    @Mock
    private DirectorsReportTransformer transformer;

    @Mock
    private DirectorsReportRepository repository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private DirectorService directorService;

    @Mock
    private SecretaryService secretaryService;

    @Mock
    private StatementsService statementsService;

    @Mock
    private DirectorsApprovalService directorsApprovalService;

    @Mock
    private DirectorsReport directorsReport;

    @Mock
    private Transaction transaction;

    @Mock
    private DirectorsReportEntity directorsReportEntity;

    @Mock
    private DirectorsReportDataEntity directorsDataEntity;

    @Mock
    private Map<String, String> links;

    @Mock
    private Map<String, String> director;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TransactionLinks transactionLinks;

    @InjectMocks
    private DirectorsReportServiceImpl service;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "selfLink";
    private static final String DIRECTORS_ID = "directorsId";

    @Test
    @DisplayName("Tests successful creation of a directors report")
    void createDirectorsReportSuccess() throws DataException {
        when(transformer.transform(directorsReport)).thenReturn(directorsReportEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(directorsReport.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(SELF_LINK);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<DirectorsReport> response =
                service.create(directorsReport, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(directorsReport, response.getData());

        verify(directorsReport).setDirectors(null);
    }

    @Test
    @DisplayName("Tests the creation of a Directors Report where the repository throws a duplicate key exception")
    void createDirectorsReportDuplicateKeyException() throws DataException {
        when(transformer.transform(directorsReport)).thenReturn(directorsReportEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(directorsReportEntity)).thenThrow(DuplicateKeyException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<DirectorsReport> response =
                service.create(directorsReport, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());

        verify(directorsReport).setDirectors(null);
    }

    @Test
    @DisplayName("Tests the creation of a Directors Report where the repository throws a mongo exception")
    void createDirectorsReportMongoException() throws DataException {
        when(transformer.transform(directorsReport)).thenReturn(directorsReportEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(directorsReportEntity)).thenThrow(MongoException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class, () ->
                service.create(directorsReport, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink();

        verify(directorsReport).setDirectors(null);
    }

    @Test
    @DisplayName("Tests the successful retrieval of a Directors Report")
    void getDirectorsReportSuccess() throws DataException {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(directorsReportEntity));
        when(transformer.transform(directorsReportEntity)).thenReturn(directorsReport);

        ResponseObject<DirectorsReport> response = service.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(directorsReport, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent Directors Report")
    void getDirectorsReportNotFound() throws DataException {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        ResponseObject<DirectorsReport> response = service.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a Directors Report where the repository throws a MongoException")
    void getDirectorsReportThrowsMongoException() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> service.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful deletion of a Directors Report")
    void deleteDirectorsReportSuccess() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<DirectorsReport> response = service.delete(COMPANY_ACCOUNTS_ID, request);

        assertChildResourcesDeleted();
        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a non existent Directors Report")
    void deleteDirectorsReportNotFound() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<DirectorsReport> response = service.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertWhetherSmallFullServiceCalledToRemoveLink(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a Directors Report where the repository throws a MongoException")
    void deleteDirectorsReportThrowsMongoException() {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () -> service.delete(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests successful removal of a Director")
    void removeDirectorSuccess() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(directorsReportEntity));

        when(directorsReportEntity.getData()).thenReturn(directorsDataEntity);
        when(directorsDataEntity.getDirectors()).thenReturn(director);

        assertAll(() -> service.removeDirector(COMPANY_ACCOUNTS_ID, DIRECTORS_ID, request));

        verify(director, times(1)).remove(DIRECTORS_ID);
        verify(repository, times(1)).save(directorsReportEntity);
    }

    @Test
    @DisplayName("Tests removal of a director and the repository throws a Mongo exception")
    void removeDirectorDataException() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(directorsReportEntity));

        when(directorsReportEntity.getData()).thenReturn(directorsDataEntity);
        when(directorsDataEntity.getDirectors()).thenReturn(director);

        when(repository.save(directorsReportEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> service.removeDirector(COMPANY_ACCOUNTS_ID, DIRECTORS_ID, request));

        verify(director, times(1)).remove(DIRECTORS_ID);
    }

    @Test
    @DisplayName("Tests successful creation of a Director")
    void addDirectorSuccess() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(directorsReportEntity));

        when(directorsReportEntity.getData()).thenReturn(directorsDataEntity);
        when(directorsDataEntity.getDirectors()).thenReturn(director);

        assertAll(() -> service.addDirector(COMPANY_ACCOUNTS_ID, DIRECTORS_ID, SELF_LINK, request));

        verify(repository, times(1)).save(directorsReportEntity);

    }

    @Test
    @DisplayName("Tests the successful addition of a directors report link")
    void addLinkSuccess() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(directorsReportEntity));

        when(directorsReportEntity.getData()).thenReturn(directorsDataEntity);
        when(directorsDataEntity.getLinks()).thenReturn(links);

        DirectorsReportLinkType directorsReportLinkType = DirectorsReportLinkType.SELF;

        assertAll(() -> service.addLink(COMPANY_ACCOUNTS_ID, directorsReportLinkType, SELF_LINK, request));

        verify(links, times(1)).put(directorsReportLinkType.getLink(), SELF_LINK);
    }

    @Test
    @DisplayName("Tests the  addition of a directors report link where the repository throws a Mongo exception")
    void addLinkMongoException() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(directorsReportEntity));

        when(directorsReportEntity.getData()).thenReturn(directorsDataEntity);
        when(directorsDataEntity.getLinks()).thenReturn(links);

        when(repository.save(directorsReportEntity)).thenThrow(MongoException.class);

        DirectorsReportLinkType directorsReportLinkType = DirectorsReportLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.addLink(COMPANY_ACCOUNTS_ID, directorsReportLinkType, SELF_LINK, request));

        verify(links, times(1)).put(directorsReportLinkType.getLink(), SELF_LINK);
    }

    @Test
    @DisplayName("Tests the  addition of a directors report link where the entity is not found")
    void addLinkDirectorsReportEntityNotFound() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        DirectorsReportEntity directorsReportEntity = null;
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        DirectorsReportLinkType directorsReportLinkType = DirectorsReportLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.addLink(COMPANY_ACCOUNTS_ID, directorsReportLinkType, SELF_LINK, request));

        verify(repository, never()).save(directorsReportEntity);
    }

    @Test
    @DisplayName("Tests the successful removal of a directors report link")
    void removeLinkSuccess() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(directorsReportEntity));

        when(directorsReportEntity.getData()).thenReturn(directorsDataEntity);
        when(directorsDataEntity.getLinks()).thenReturn(links);

        DirectorsReportLinkType directorsReportLinkType = DirectorsReportLinkType.SELF;

        assertAll(() -> service.removeLink(COMPANY_ACCOUNTS_ID, directorsReportLinkType, request));

        verify(links, times(1)).remove(directorsReportLinkType.getLink());
    }

    @Test
    @DisplayName("Tests the  removal of a directors report link where the repository throws a Mongo exception")
    void removeLinkMongoException() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(directorsReportEntity));

        when(directorsReportEntity.getData()).thenReturn(directorsDataEntity);
        when(directorsDataEntity.getLinks()).thenReturn(links);

        when(repository.save(directorsReportEntity)).thenThrow(MongoException.class);

        DirectorsReportLinkType directorsReportLinkType = DirectorsReportLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.removeLink(COMPANY_ACCOUNTS_ID, directorsReportLinkType, request));

        verify(links, times(1)).remove(directorsReportLinkType.getLink());
    }

    @Test
    @DisplayName("Tests the  removal of a directors report link where the entity is not found")
    void removeLinkDirectorsReportEntityNotFound() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.DIRECTORS_REPORT.getName()))
                .thenReturn(GENERATED_ID);

        DirectorsReportEntity directorsReportEntity = null;
        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        DirectorsReportLinkType directorsReportLinkType = DirectorsReportLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.removeLink(COMPANY_ACCOUNTS_ID, directorsReportLinkType, request));

        verify(repository, never()).save(directorsReportEntity);
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(directorsReportEntity, times(1)).setId(GENERATED_ID);
    }

    private void assertMetaDataSetOnRestObject() {
        verify(directorsReport, times(1)).setKind(anyString());
        verify(directorsReport, times(1)).setEtag(anyString());
        verify(directorsReport, times(1)).setLinks(anyMap());
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(directorsReportEntity);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository, times(1)).deleteById(GENERATED_ID);
    }

    private void assertRepositoryFindByIdCalled() {
        verify(repository, times(1)).findById(GENERATED_ID);
    }

    private void assertChildResourcesDeleted() throws DataException {
        verify(directorService, times(1)).deleteAll(transaction, COMPANY_ACCOUNTS_ID, request);
        verify(secretaryService, times(1)).delete(COMPANY_ACCOUNTS_ID, request);
        verify(statementsService, times(1)).delete(COMPANY_ACCOUNTS_ID, request);
        verify(directorsApprovalService, times(1)).delete(COMPANY_ACCOUNTS_ID, request);
    }

    private void assertWhetherSmallFullServiceCalledToAddLink() throws DataException {
        verify(smallFullService, never())
                .addLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.DIRECTORS_REPORT, SELF_LINK, request);
    }

    private void assertWhetherSmallFullServiceCalledToRemoveLink(boolean isServiceExpected) throws DataException {
        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(smallFullService, timesExpected)
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.DIRECTORS_REPORT, request);
    }
}
