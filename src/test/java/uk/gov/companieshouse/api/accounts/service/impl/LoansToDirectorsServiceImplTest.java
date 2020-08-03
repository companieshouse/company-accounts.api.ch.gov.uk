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
import uk.gov.companieshouse.api.accounts.links.LoansToDirectorsLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoansToDirectorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoansToDirectorsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.repository.smallfull.LoansToDirectorsRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.LoansToDirectorsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

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

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoansToDirectorsServiceImplTest {

    @Mock
    private LoansToDirectorsTransformer transformer;

    @Mock
    private LoansToDirectorsRepository repository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private LoansToDirectors loansToDirectors;

    @Mock
    private Transaction transaction;

    @Mock
    private LoansToDirectorsEntity loansToDirectorsEntity;

    @Mock
    private LoansToDirectorsDataEntity loansToDirectorsDataEntity;

    @Mock
    private Map<String, String> links;

    @Mock
    private Map<String, String> loans;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private LoanServiceImpl loanService;

    @Mock
    private LoansToDirectorsAdditionalInformationService additionalInformationService;

    @InjectMocks
    private LoansToDirectorsServiceImpl service;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "selfLink";
    private static final String LOAN_ID = "loanId";

    @Test
    @DisplayName("Tests successful creation of a loans to directors resource")
    void createLoansToDirectorsSuccess() throws DataException {

        when(transformer.transform(loansToDirectors)).thenReturn(loansToDirectorsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(loansToDirectors.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(SELF_LINK);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<LoansToDirectors> response =
                service.create(loansToDirectors, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(loansToDirectors, response.getData());

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(true);

        verify(loansToDirectors).setLoans(null);
    }

    @Test
    @DisplayName("Tests the creation of a loans to directors resource where the repository throws a duplicate key exception")
    void createLoansToDirectorsDuplicateKeyException() throws DataException {

        when(transformer.transform(loansToDirectors)).thenReturn(loansToDirectorsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(loansToDirectorsEntity)).thenThrow(DuplicateKeyException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<LoansToDirectors> response =
                service.create(loansToDirectors, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());

        assertWhetherSmallFullServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the creation of a loans to directors resource where the repository throws a mongo exception")
    void createLoansToDirectorsMongoException() throws DataException {

        when(transformer.transform(loansToDirectors)).thenReturn(loansToDirectorsEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);
        when(repository.insert(loansToDirectorsEntity)).thenThrow(MongoException.class);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class, () ->
                service.create(loansToDirectors, transaction, COMPANY_ACCOUNTS_ID, request));
        
        assertWhetherSmallFullServiceCalledToAddLink(false);
    }

    @Test
    @DisplayName("Tests the successful retrieval of a loans to directors resource")
    void getLoansToDirectorsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(loansToDirectorsEntity));
        when(transformer.transform(loansToDirectorsEntity)).thenReturn(loansToDirectors);

        ResponseObject<LoansToDirectors> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(loansToDirectors, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent loans to directors resource")
    void getLoansToDirectorsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        ResponseObject<LoansToDirectors> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a loans to directors resource where the repository throws a MongoException")
    void getLoansToDirectorsThrowsMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful deletion of a loans to directors resource")
    void deleteLoansToDirectorsSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<LoansToDirectors> response =
                service.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());

        verify(loanService).deleteAll(transaction, COMPANY_ACCOUNTS_ID, request);
        verify(additionalInformationService).delete(COMPANY_ACCOUNTS_ID, request);
    }

    @Test
    @DisplayName("Tests the deletion of a non existent loans to directors resource")
    void deleteLoansToDirectorsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<LoansToDirectors> response =
                service.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertWhetherSmallFullServiceCalledToRemoveLink(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a loans to directors resource where the repository throws a MongoException")
    void deleteLoansToDirectorsThrowsMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                service.delete(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests successful removal of a loan")
    void removeLoanSuccess() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(loansToDirectorsEntity));

        when(loansToDirectorsEntity.getData()).thenReturn(loansToDirectorsDataEntity);
        when(loansToDirectorsDataEntity.getLoans()).thenReturn(loans);

        assertAll(() -> service.removeLoan(COMPANY_ACCOUNTS_ID, LOAN_ID, request));

        verify(loans).remove(LOAN_ID);
        verify(repository).save(loansToDirectorsEntity);
    }

    @Test
    @DisplayName("Tests removal of a loan when the repository throws a Mongo exception")
    void removeLoanDataException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(loansToDirectorsEntity));

        when(loansToDirectorsEntity.getData()).thenReturn(loansToDirectorsDataEntity);
        when(loansToDirectorsDataEntity.getLoans()).thenReturn(loans);

        when(repository.save(loansToDirectorsEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class,
                () -> service.removeLoan(COMPANY_ACCOUNTS_ID, LOAN_ID, request));

        verify(loans).remove(LOAN_ID);
    }

    @Test
    @DisplayName("Tests removal of a loan when the resource is not found")
    void removeLoanResourceNotFound() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        assertThrows(DataException.class,
                () -> service.removeLoan(COMPANY_ACCOUNTS_ID, LOAN_ID, request));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Tests successful creation of a loan")
    void addLoanSuccess() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(loansToDirectorsEntity));

        when(loansToDirectorsEntity.getData()).thenReturn(loansToDirectorsDataEntity);
        when(loansToDirectorsDataEntity.getLoans()).thenReturn(loans);

        assertAll(() -> service.addLoan(COMPANY_ACCOUNTS_ID, LOAN_ID, SELF_LINK, request));

        verify(loans).put(LOAN_ID, SELF_LINK);
        verify(repository).save(loansToDirectorsEntity);
    }

    @Test
    @DisplayName("Tests creation of a loan when the repository throws a Mongo exception")
    void addLoanMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(loansToDirectorsEntity));

        when(loansToDirectorsEntity.getData()).thenReturn(loansToDirectorsDataEntity);
        when(loansToDirectorsDataEntity.getLoans()).thenReturn(loans);

        when(repository.save(loansToDirectorsEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class,
                () -> service.addLoan(COMPANY_ACCOUNTS_ID, LOAN_ID, SELF_LINK, request));

        verify(loans).put(LOAN_ID, SELF_LINK);
    }

    @Test
    @DisplayName("Tests creation of a loan when the resource is not found")
    void addLoanResourceNotFound() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        assertThrows(DataException.class,
                () -> service.addLoan(COMPANY_ACCOUNTS_ID, LOAN_ID, SELF_LINK, request));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Tests the successful addition of a loans to directors resource link")
    void addLinkSuccess() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(loansToDirectorsEntity));

        when(loansToDirectorsEntity.getData()).thenReturn(loansToDirectorsDataEntity);
        when(loansToDirectorsDataEntity.getLinks()).thenReturn(links);

        LoansToDirectorsLinkType loansToDirectorsLinkType = LoansToDirectorsLinkType.SELF;

        assertAll(() -> service.addLink(COMPANY_ACCOUNTS_ID, loansToDirectorsLinkType, SELF_LINK, request));

        verify(links).put(loansToDirectorsLinkType.getLink(), SELF_LINK);
        verify(repository).save(loansToDirectorsEntity);
    }

    @Test
    @DisplayName("Tests the  addition of a loans to directors resource link where the repository throws a Mongo exception")
    void addLinkMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(loansToDirectorsEntity));

        when(loansToDirectorsEntity.getData()).thenReturn(loansToDirectorsDataEntity);
        when(loansToDirectorsDataEntity.getLinks()).thenReturn(links);

        when(repository.save(loansToDirectorsEntity)).thenThrow(MongoException.class);

        LoansToDirectorsLinkType loansToDirectorsLinkType = LoansToDirectorsLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.addLink(COMPANY_ACCOUNTS_ID, loansToDirectorsLinkType, SELF_LINK, request));

        verify(links).put(loansToDirectorsLinkType.getLink(), SELF_LINK);
    }

    @Test
    @DisplayName("Tests the  addition of a loans to directors resource link where the entity is not found")
    void addLinkLoansToDirectorsEntityNotFound() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        LoansToDirectorsLinkType loansToDirectorsLinkType = LoansToDirectorsLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.addLink(COMPANY_ACCOUNTS_ID, loansToDirectorsLinkType, SELF_LINK, request));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Tests the successful removal of a loans to directors resource link")
    void removeLinkSuccess() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(loansToDirectorsEntity));

        when(loansToDirectorsEntity.getData()).thenReturn(loansToDirectorsDataEntity);
        when(loansToDirectorsDataEntity.getLinks()).thenReturn(links);

        LoansToDirectorsLinkType loansToDirectorsLinkType = LoansToDirectorsLinkType.SELF;

        assertAll(() -> service.removeLink(COMPANY_ACCOUNTS_ID, loansToDirectorsLinkType, request));

        verify(links).remove(loansToDirectorsLinkType.getLink());
        verify(repository).save(loansToDirectorsEntity);
    }

    @Test
    @DisplayName("Tests the  removal of a loans to directors resource link where the repository throws a Mongo exception")
    void removeLinkMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(loansToDirectorsEntity));

        when(loansToDirectorsEntity.getData()).thenReturn(loansToDirectorsDataEntity);
        when(loansToDirectorsDataEntity.getLinks()).thenReturn(links);

        when(repository.save(loansToDirectorsEntity)).thenThrow(MongoException.class);

        LoansToDirectorsLinkType loansToDirectorsLinkType = LoansToDirectorsLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.removeLink(COMPANY_ACCOUNTS_ID, loansToDirectorsLinkType, request));

        verify(links).remove(loansToDirectorsLinkType.getLink());
    }

    @Test
    @DisplayName("Tests the  removal of a loans to directors resource link where the entity is not found")
    void removeLinkLoansToDirectorsEntityNotFound() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.LOANS_TO_DIRECTORS.getName()))
                .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        LoansToDirectorsLinkType loansToDirectorsLinkType = LoansToDirectorsLinkType.SELF;

        assertThrows(DataException.class,
                () -> service.removeLink(COMPANY_ACCOUNTS_ID, loansToDirectorsLinkType, request));

        verify(repository, never()).save(any());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(loansToDirectorsEntity).setId(GENERATED_ID);
    }

    private void assertMetaDataSetOnRestObject() {
        verify(loansToDirectors).setKind(anyString());
        verify(loansToDirectors).setEtag(anyString());
        verify(loansToDirectors).setLinks(anyMap());
    }

    private void assertRepositoryInsertCalled() {
        verify(repository).insert(loansToDirectorsEntity);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository).deleteById(GENERATED_ID);
    }

    private void assertWhetherSmallFullServiceCalledToAddLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(smallFullService, timesExpected)
                .addLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.LOANS_TO_DIRECTORS, SELF_LINK, request);
    }

    private void assertWhetherSmallFullServiceCalledToRemoveLink(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(smallFullService, timesExpected)
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.LOANS_TO_DIRECTORS, request);
    }
}
