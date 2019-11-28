package uk.gov.companieshouse.api.accounts.service.impl;

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
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.repository.DirectorRepository;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.DirectorTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DirectorServiceTest {

    @Mock
    private DirectorTransformer transformer;

    @Mock
    private DirectorRepository repository;

    @Mock
    private DirectorsReportService directorsReportService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private Director director;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private HttpServletRequest request;

    @Mock
    private DirectorEntity directorEntity;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private DirectorService directorService;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String DIRECTOR_ID = "directorId";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String DIRECTOR_SELF_LINK = "directorSelfLink";

    private static final String URI = "/transactions/transactionId/company-accounts/" +
                                        COMPANY_ACCOUNTS_ID + "/small-full/directors-report" +
                                        "/directors/" + DIRECTOR_ID;

    @Test
    @DisplayName("Tests the successful creation of a director resource")
    void createDirectorSuccess() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(director.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(DIRECTOR_SELF_LINK);

        ResponseObject<Director> response =
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(director, response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a director resource where the repository throws a duplicate key exception")
    void createDirectorDuplicateKeyException() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(directorEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<Director> response =
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a director resource where the repository throws a Mongo exception")
    void createDirectorMongoException() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(directorEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(false);
    }

    @Test
    @DisplayName("Tests the successful update of a director resource")
    void updateDirectorSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        ResponseObject<Director> response =
                directorService.update(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(director, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a director resource where the repository throws a Mongo exception")
    void updateDirectorMongoException() {

        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.save(directorEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                directorService.update(director, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
    }

    @Test
    @DisplayName("Tests the successful retrieval of a director resource")
    void getDirectorSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.findById(DIRECTOR_ID)).thenReturn(Optional.of(directorEntity));
        when(transformer.transform(directorEntity)).thenReturn(director);

        ResponseObject<Director> response =
                directorService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(director, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent director resource")
    void getDirectorNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        DirectorEntity directorEntity = null;
        when(repository.findById(DIRECTOR_ID)).thenReturn(Optional.ofNullable(directorEntity));

        ResponseObject<Director> response =
                directorService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a director resource where the repository throws a Mongo exception")
    void getDirectorMongoException() {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.findById(DIRECTOR_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                directorService.find(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryFindByIdCalled();
    }

    @Test
    @DisplayName("Tests the successful deletion of a director resource")
    void deleteDirectorSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(DIRECTOR_ID)).thenReturn(true);

        ResponseObject<Director> response =
                directorService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveDirector(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a director resource where the repository throws a Mongo exception")
    void deleteDirectorMongoException() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(DIRECTOR_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(DIRECTOR_ID);

        assertThrows(DataException.class, () ->
                directorService.delete(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveDirector(false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent director resource")
    void deleteDirectorNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(DIRECTOR_ID)).thenReturn(false);

        ResponseObject<Director> response =
                directorService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(DIRECTOR_ID);
        assertWhetherDirectorsReportServiceCalledToRemoveDirector(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    private void assertMetaDataSetOnRestObject() {
        verify(director, times(1)).setKind(Kind.DIRECTORS_REPORT_DIRECTOR.getValue());
        verify(director, times(1)).setEtag(anyString());
        verify(director, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(directorEntity, times(1)).setId(DIRECTOR_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(directorEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(directorEntity);
    }

    private void assertRepositoryFindByIdCalled() {
        verify(repository, times(1)).findById(DIRECTOR_ID);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository, times(1)).deleteById(DIRECTOR_ID);
    }

    private void assertWhetherDirectorsReportServiceCalledToAddDirector(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(directorsReportService, timesExpected)
                .addDirector(COMPANY_ACCOUNTS_ID, DIRECTOR_ID, DIRECTOR_SELF_LINK, request);
    }

    private void assertWhetherDirectorsReportServiceCalledToRemoveDirector(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(directorsReportService, timesExpected)
                .removeDirector(COMPANY_ACCOUNTS_ID, DIRECTOR_ID, request);
    }

}