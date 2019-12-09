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
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.DirectorsReportLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.DirectorsApprovalRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.DirectorsApprovalTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorsApprovalServiceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private DirectorsApproval directorsApproval;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private DirectorsApprovalRepository directorsApprovalRepository;

    @Mock
    private DirectorsReportServiceImpl directorsReportService;

    @Mock
    private DirectorsApprovalEntity directorsApprovalEntity;

    @Mock
    private DirectorsApprovalTransformer directorsApprovalTransformer;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private Errors errors;

    @InjectMocks
    private DirectorsApprovalService directorsApprovalService;

    private static final String SELF_LINK = "self_link";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String RESOURCE_ID = "resourceId";
    private static final String APPROVAL = "approval";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";

    @Test
    @DisplayName("Tests the successful creation of an directors approval resource")
    public void canCreateADirectorsApproval() throws DataException {

        when(directorsApprovalTransformer.transform(directorsApproval)).thenReturn(directorsApprovalEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<DirectorsApproval> result = directorsApprovalService
                .create(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(directorsApproval, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a directors approval resource")
    public void createDirectorsApprovalDuplicateKey() throws DataException {
        doReturn(directorsApprovalEntity).when(directorsApprovalTransformer).transform(any(DirectorsApproval.class));
        when(directorsApprovalRepository.insert(directorsApprovalEntity)).thenThrow(duplicateKeyException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject response = directorsApprovalService.create(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating an directors approval")
    void createDirectorsApprovalMongoExceptionFailure() throws DataException {
        doReturn(directorsApprovalEntity).when(directorsApprovalTransformer).transform(any(DirectorsApproval.class));
        when(directorsApprovalRepository.insert(directorsApprovalEntity)).thenThrow(mongoException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class,
                () -> directorsApprovalService.create(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful find of an directors approval resource")
    public void findDirectorsApproval() throws DataException {

        when(keyIdGenerator
                .generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(directorsApprovalRepository.findById(RESOURCE_ID))
                .thenReturn(Optional.ofNullable(directorsApprovalEntity));
        when(directorsApprovalTransformer.transform(directorsApprovalEntity)).thenReturn(directorsApproval);
        ResponseObject<DirectorsApproval> result = directorsApprovalService
                .find(COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(directorsApproval, result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of an director approval resource")
    public void findDirectorsApprovalMongoException() {

        when(keyIdGenerator
                .generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(directorsApprovalRepository.findById(RESOURCE_ID)).thenThrow(mongoException);
        assertThrows(DataException.class, () -> directorsApprovalService.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of an director approval resource")
    public void findDirectorsApprovalNotFound() throws DataException {

        when(keyIdGenerator
                .generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(directorsApprovalRepository.findById(RESOURCE_ID)).thenReturn(null);
        DirectorsApprovalEntity directorsApprovalEntity = null;
        when(directorsApprovalRepository.findById(RESOURCE_ID)).thenReturn(Optional.ofNullable(directorsApprovalEntity));

        ResponseObject<DirectorsApproval> response =
                directorsApprovalService.find(COMPANY_ACCOUNTS_ID, request);

        verify(directorsApprovalRepository, times(1)).findById(RESOURCE_ID);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the successful update of a directors approval resource")
    void updateDirectorsApprovalSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(directorsApprovalTransformer.transform(directorsApproval)).thenReturn(directorsApprovalEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        ResponseObject<DirectorsApproval> response =
                directorsApprovalService.update(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(directorsApproval, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a directors approval resource where the repository throws a Mongo exception")
    void updateDirectorsApprovalMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(directorsApprovalTransformer.transform(directorsApproval)).thenReturn(directorsApprovalEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(directorsApprovalRepository.save(directorsApprovalEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                directorsApprovalService.update(directorsApproval, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
    }

    @Test
    @DisplayName("Tests the successful deletion of a directors approval resource")
    void deleteDirectorsApprovalSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(directorsApprovalRepository.existsById(RESOURCE_ID)).thenReturn(true);

        ResponseObject<DirectorsApproval> response =
                directorsApprovalService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveLink(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a directors approval resource where the repository throws a Mongo exception")
    void deleteDirectorsApprovalMongoException() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(directorsApprovalRepository.existsById(RESOURCE_ID)).thenReturn(true);
        doThrow(MongoException.class).when(directorsApprovalRepository).deleteById(RESOURCE_ID);

        assertThrows(DataException.class, () ->
                directorsApprovalService.delete(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveLink(false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent directors approval resource")
    void deleteDirectorsApprovalNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(directorsApprovalRepository.existsById(RESOURCE_ID)).thenReturn(false);

        ResponseObject<DirectorsApproval> response =
                directorsApprovalService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(directorsApprovalRepository, never()).deleteById(RESOURCE_ID);
        assertWhetherDirectorsReportServiceCalledToRemoveLink(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    private void assertMetaDataSetOnRestObject() {
        verify(directorsApproval, times(1)).setKind(Kind.DIRECTORS_REPORT_APPROVAL.getValue());
        verify(directorsApproval, times(1)).setEtag(anyString());
        verify(directorsApproval, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(directorsApprovalEntity, times(1)).setId(RESOURCE_ID);
    }

    private void assertRepositoryUpdateCalled() {
        verify(directorsApprovalRepository, times(1)).save(directorsApprovalEntity);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(directorsApprovalRepository, times(1)).deleteById(RESOURCE_ID);
    }

    private void assertWhetherDirectorsReportServiceCalledToRemoveLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(directorsReportService, timesExpected)
                .removeLink(COMPANY_ACCOUNTS_ID, DirectorsReportLinkType.APPROVAL, request);
    }


}