package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.DirectorsReportLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.SecretaryEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Secretary;
import uk.gov.companieshouse.api.accounts.repository.SecretaryRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.SecretaryTransformer;
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
public class SecretaryServiceTest {

    @Mock
    private SecretaryTransformer transformer;

    @Mock
    private SecretaryRepository repository;

    @Mock
    private DirectorsReportServiceImpl directorsReportService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private Secretary secretary;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecretaryEntity secretaryEntity;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private SecretaryService secretaryService;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String SECRETARY_ID = "secretaryId";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String SECRETARY_SELF_LINK = "secretarySelfLink";

    private static final String URI = "/transactions/transactionId/company-accounts/" +
            COMPANY_ACCOUNTS_ID + "/small-full/directors-report" +
            "/secretaries/" + SECRETARY_ID;

    @Test
    @DisplayName("Tests the successful creation of a secretary resource")
    void createSecretarySuccess() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(SECRETARY_ID);

        when(transformer.transform(secretary)).thenReturn(secretaryEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(secretary.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(SECRETARY_SELF_LINK);

        ResponseObject<Secretary> response =
                secretaryService.create(secretary, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddLink(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(secretary, response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a secretary resource where the repository throws a duplicate key exception")
    void createSecretaryDuplicateKeyException() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(SECRETARY_ID);

        when(transformer.transform(secretary)).thenReturn(secretaryEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(secretaryEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<Secretary> response =
                secretaryService.create(secretary, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddLink(false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a secretary resource where the repository throws a Mongo exception")
    void createSecretaryMongoException() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(SECRETARY_ID);

        when(transformer.transform(secretary)).thenReturn(secretaryEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(secretaryEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                secretaryService.create(secretary, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the successful update of a secretary resource")
    void updateSecretarySuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(secretary)).thenReturn(secretaryEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        ResponseObject<Secretary> response =
                secretaryService.update(secretary, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(secretary, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a secretary resource where the repository throws a Mongo exception")
    void updateSecretaryMongoException() {

        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(secretary)).thenReturn(secretaryEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.save(secretaryEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                secretaryService.update(secretary, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
    }

    @Test
    @DisplayName("Tests the successful retrieval of a secretary resource")
    void getSecretarySuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.findById(SECRETARY_ID)).thenReturn(Optional.of(secretaryEntity));
        when(transformer.transform(secretaryEntity)).thenReturn(secretary);

        ResponseObject<Secretary> response =
                secretaryService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(secretary, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent secretary resource")
    void getSecretaryNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        SecretaryEntity secretaryEntity = null;
        when(repository.findById(SECRETARY_ID)).thenReturn(Optional.ofNullable(secretaryEntity));

        ResponseObject<Secretary> response =
                secretaryService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a secretary resource where the repository throws a Mongo exception")
    void getSecretaryMongoException() {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.findById(SECRETARY_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                secretaryService.find(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryFindByIdCalled();
    }

    @Test
    @DisplayName("Tests the successful deletion of a secretary resource")
    void deleteSecretarySuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(SECRETARY_ID)).thenReturn(true);

        ResponseObject<Secretary> response =
                secretaryService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveLink(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a secretary resource where the repository throws a Mongo exception")
    void deleteSecretaryMongoException() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(SECRETARY_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(SECRETARY_ID);

        assertThrows(DataException.class, () ->
                secretaryService.delete(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveLink(false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent secretary resource")
    void deleteSecretaryNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(SECRETARY_ID)).thenReturn(false);

        ResponseObject<Secretary> response =
                secretaryService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(SECRETARY_ID);
        assertWhetherDirectorsReportServiceCalledToRemoveLink(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    private void assertMetaDataSetOnRestObject() {
        verify(secretary, times(1)).setKind(Kind.DIRECTORS_REPORT_SECRETARY.getValue());
        verify(secretary, times(1)).setEtag(anyString());
        verify(secretary, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(secretaryEntity, times(1)).setId(SECRETARY_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(secretaryEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(secretaryEntity);
    }

    private void assertRepositoryFindByIdCalled() {
        verify(repository, times(1)).findById(SECRETARY_ID);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository, times(1)).deleteById(SECRETARY_ID);
    }

    private void assertWhetherDirectorsReportServiceCalledToAddLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(directorsReportService, timesExpected)
                .addLink(COMPANY_ACCOUNTS_ID, DirectorsReportLinkType.SECRETARY, SECRETARY_SELF_LINK, request);
    }

    private void assertWhetherDirectorsReportServiceCalledToRemoveLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(directorsReportService, timesExpected)
                .removeLink(COMPANY_ACCOUNTS_ID, DirectorsReportLinkType.SECRETARY, request);
    }

}