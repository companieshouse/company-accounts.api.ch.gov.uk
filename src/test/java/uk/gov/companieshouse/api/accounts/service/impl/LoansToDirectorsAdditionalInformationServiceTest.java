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
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.LoansToDirectorsLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.AdditionalInformationEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.AdditionalInformation;
import uk.gov.companieshouse.api.accounts.repository.smallfull.LoansToDirectorsAdditionalInformationRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.LoansToDirectorsAdditionalInformationTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
class LoansToDirectorsAdditionalInformationServiceTest {

    @Mock
    private LoansToDirectorsAdditionalInformationTransformer transformer;

    @Mock
    private LoansToDirectorsAdditionalInformationRepository repository;

    @Mock
    private LoansToDirectorsServiceImpl loansToDirectorsService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private AdditionalInformation additionalInformation;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private HttpServletRequest request;

    @Mock
    private AdditionalInformationEntity additionalInformationEntity;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private LoansToDirectorsAdditionalInformationService loansToDirectorsAdditionalInformationService;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String ADDITIONAL_INFO_SELF_LINK = "additionalInformationSelfLink";

    @BeforeEach
    private void setup() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName() + "-" + ResourceName.ADDITIONAL_INFO.getName()))
                .thenReturn(GENERATED_ID);
    }

    @Test
    @DisplayName("Tests the successful creation of an additionalInformation resource")
    void createAdditionalInformationSuccess() throws DataException {

        when(transformer.transform(additionalInformation)).thenReturn(additionalInformationEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(additionalInformation.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(ADDITIONAL_INFO_SELF_LINK);

        ResponseObject<AdditionalInformation> response =
                loansToDirectorsAdditionalInformationService
                        .create(additionalInformation, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherLoansToDirectorsServiceCalledToAddLink(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(additionalInformation, response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a additionalInformation resource where the repository throws a duplicate key exception")
    void createAdditionalInformationDuplicateKeyException() throws DataException {

        when(transformer.transform(additionalInformation)).thenReturn(additionalInformationEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(additionalInformationEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<AdditionalInformation> response =
                loansToDirectorsAdditionalInformationService
                        .create(additionalInformation, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertWhetherLoansToDirectorsServiceCalledToAddLink(false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a additionalInformation resource where the repository throws a Mongo exception")
    void createAdditionalInformationMongoException() throws DataException {

        when(transformer.transform(additionalInformation)).thenReturn(additionalInformationEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(additionalInformationEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                loansToDirectorsAdditionalInformationService
                        .create(additionalInformation, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertWhetherLoansToDirectorsServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the successful update of a additionalInformation resource")
    void updateAdditionalInformationSuccess() throws DataException {

        when(transformer.transform(additionalInformation)).thenReturn(additionalInformationEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        ResponseObject<AdditionalInformation> response =
                loansToDirectorsAdditionalInformationService
                        .update(additionalInformation, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(additionalInformation, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a additionalInformation resource where the repository throws a Mongo exception")
    void updateAdditionalInformationMongoException() {

        when(transformer.transform(additionalInformation)).thenReturn(additionalInformationEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.save(additionalInformationEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                loansToDirectorsAdditionalInformationService
                        .update(additionalInformation, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
    }

    @Test
    @DisplayName("Tests the successful retrieval of a additionalInformation resource")
    void getAdditionalInformationSuccess() throws DataException {

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(additionalInformationEntity));
        when(transformer.transform(additionalInformationEntity)).thenReturn(additionalInformation);

        ResponseObject<AdditionalInformation> response =
                loansToDirectorsAdditionalInformationService.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(additionalInformation, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent additionalInformation resource")
    void getAdditionalInformationNotFound() throws DataException {

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        ResponseObject<AdditionalInformation> response =
                loansToDirectorsAdditionalInformationService.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a additionalInformation resource where the repository throws a Mongo exception")
    void getAdditionalInformationMongoException() {

        when(repository.findById(GENERATED_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                loansToDirectorsAdditionalInformationService.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful deletion of a additionalInformation resource")
    void deleteAdditionalInformationSuccess() throws DataException {

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<AdditionalInformation> response =
                loansToDirectorsAdditionalInformationService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherLoansToDirectorsServiceCalledToRemoveLink(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a additionalInformation resource where the repository throws a Mongo exception")
    void deleteAdditionalInformationMongoException() throws DataException {

        when(repository.existsById(GENERATED_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                loansToDirectorsAdditionalInformationService.delete(COMPANY_ACCOUNTS_ID, request));

        assertWhetherLoansToDirectorsServiceCalledToRemoveLink(false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent additionalInformation resource")
    void deleteAdditionalInformationNotFound() throws DataException {

        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<AdditionalInformation> response =
                loansToDirectorsAdditionalInformationService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertWhetherLoansToDirectorsServiceCalledToRemoveLink(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    private void assertMetaDataSetOnRestObject() {
        verify(additionalInformation, times(1)).setKind(Kind.LOANS_TO_DIRECTORS_ADDITIONAL_INFO.getValue());
        verify(additionalInformation, times(1)).setEtag(anyString());
        verify(additionalInformation, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(additionalInformationEntity, times(1)).setId(GENERATED_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(additionalInformationEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(additionalInformationEntity);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository, times(1)).deleteById(GENERATED_ID);
    }

    private void assertWhetherLoansToDirectorsServiceCalledToAddLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(loansToDirectorsService, timesExpected)
                .addLink(COMPANY_ACCOUNTS_ID, LoansToDirectorsLinkType.ADDITIONAL_INFO, ADDITIONAL_INFO_SELF_LINK, request);
    }

    private void assertWhetherLoansToDirectorsServiceCalledToRemoveLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(loansToDirectorsService, timesExpected)
                .removeLink(COMPANY_ACCOUNTS_ID, LoansToDirectorsLinkType.ADDITIONAL_INFO, request);
    }

}