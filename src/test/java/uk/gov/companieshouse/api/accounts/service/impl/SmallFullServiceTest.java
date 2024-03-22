package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.MissingAccountingPeriodException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.NextAccounts;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.repository.SmallFullRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.account.CompanyAccountApi;
import uk.gov.companieshouse.api.model.company.account.LastAccountsApi;
import uk.gov.companieshouse.api.model.company.account.NextAccountsApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class SmallFullServiceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private SmallFull smallFull;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private SmallFullEntity smallFullEntity;

    @Mock
    private SmallFullDataEntity smallFullDataEntity;

    @Mock
    private Map<String, String> links;

    @Mock
    private SmallFullTransformer smallFullTransformer;

    @Mock
    private SmallFullRepository smallFullRepository;

    @Mock
    private CompanyAccountService companyAccountService;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private CompanyService companyService;

    @Mock
    private CompanyProfileApi companyProfileApi;

    @Mock
    private CompanyAccountApi companyAccountApi;

    @Mock
    private NextAccounts nextAccounts;

    @Mock
    private NextAccountsApi nextAccountsApi;

    @Mock
    private LastAccountsApi lastAccountsApi;

    @Mock
    private StatementService statementService;

    @InjectMocks
    private SmallFullService smallFullService;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "self_link";
    private static final String COMPANY_NUMBER = "companyNo";

    @Test
    @DisplayName("Tests the successful creation of a smallFull resource")
    void createAccountSuccess() throws DataException, ServiceException {
        setUpKeyIdGeneratorStubbing();
        when(smallFullTransformer.transform(smallFull)).thenReturn(smallFullEntity);

        setUpTransactionStubbing();
        setUpCompanyProfileApiStubbing();

        when(smallFull.getNextAccounts()).thenReturn(nextAccounts);
        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        ResponseObject<SmallFull> result = smallFullService
                .create(smallFull, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(smallFull, result.getData());
        assertEquals(ResponseStatus.CREATED, result.getStatus());

        verify(companyAccountService).addLink(eq(COMPANY_ACCOUNTS_ID), eq(CompanyAccountLinkType.SMALL_FULL), anyString());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a smallFull resource")
    void createSmallFullDuplicateKey() throws DataException, ServiceException {
        setUpKeyIdGeneratorStubbing();
        doReturn(smallFullEntity).when(smallFullTransformer).transform(ArgumentMatchers
                .any(SmallFull.class));
        when(smallFullRepository.insert(smallFullEntity)).thenThrow(duplicateKeyException);

        setUpTransactionStubbing();
        setUpCompanyProfileApiStubbing();

        when(smallFull.getNextAccounts()).thenReturn(nextAccounts);
        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        ResponseObject<SmallFull> response = smallFullService
                .create(smallFull, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(response);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating a small full")
    void createSmallFullMongoExceptionFailure() throws ServiceException {
        setUpKeyIdGeneratorStubbing();
        doReturn(smallFullEntity).when(smallFullTransformer).transform(ArgumentMatchers
                .any(SmallFull.class));
        when(smallFullRepository.insert(smallFullEntity)).thenThrow(mongoException);

        setUpTransactionStubbing();
        setUpCompanyProfileApiStubbing();

        when(smallFull.getNextAccounts()).thenReturn(nextAccounts);
        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        assertThrows(DataException.class, () ->
                smallFullService.create(smallFull, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Create small full accounts - no next accounts on company profile")
    void createSmallFullNoNextAccounts() throws ServiceException {

        setUpTransactionStubbing();
        setUpCompanyProfileApiNoNextAccountsStubbing();

        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        assertThrows(MissingAccountingPeriodException.class,
                () -> smallFullService
                        .create(smallFull, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Create small full accounts - no accounts on company profile")
    void createSmallFullNoAccounts() throws ServiceException {

        setUpTransactionStubbing();

        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        assertThrows(MissingAccountingPeriodException.class,
                () -> smallFullService
                        .create(smallFull, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful find of a small full resource")
    void findSmallfull() throws DataException {
        setUpKeyIdGeneratorStubbing();
        when(smallFullRepository.findById(GENERATED_ID))
                .thenReturn(Optional.ofNullable(smallFullEntity));
        when(smallFullTransformer.transform(smallFullEntity)).thenReturn(smallFull);
        ResponseObject<SmallFull> result = smallFullService
                .find(COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(smallFull, result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a small full resource")
    void findSmallfullMongoException() {
        setUpKeyIdGeneratorStubbing();
        when(smallFullRepository.findById(GENERATED_ID)).thenThrow(mongoException);
        Executable executable = () -> {
            smallFullService.find(COMPANY_ACCOUNTS_ID, request);
        };
        assertThrows(DataException.class, executable);
    }

    @Test
    @DisplayName("Tests the successful removal of a small full link")
    void removeLinkSuccess() {
        setUpKeyIdGeneratorStubbing();

        when(smallFullRepository.findById(GENERATED_ID))
                .thenReturn(Optional.ofNullable(smallFullEntity));

        when(smallFullEntity.getData()).thenReturn(smallFullDataEntity);
        when(smallFullDataEntity.getLinks()).thenReturn(links);

        SmallFullLinkType smallFullLinkType = SmallFullLinkType.TANGIBLE_ASSETS_NOTE;

        assertAll(
                () -> smallFullService.removeLink(COMPANY_ACCOUNTS_ID, smallFullLinkType, request));

        verify(links, times(1)).remove(smallFullLinkType.getLink());
    }

    @Test
    @DisplayName("Tests the  removal of a small full link where the repository throws a Mongo exception")
    void removeLinkMongoException() {
        setUpKeyIdGeneratorStubbing();
        when(smallFullRepository.findById(GENERATED_ID))
                .thenReturn(Optional.ofNullable(smallFullEntity));

        when(smallFullEntity.getData()).thenReturn(smallFullDataEntity);
        when(smallFullDataEntity.getLinks()).thenReturn(links);

        when(smallFullRepository.save(smallFullEntity)).thenThrow(MongoException.class);

        SmallFullLinkType smallFullLinkType = SmallFullLinkType.TANGIBLE_ASSETS_NOTE;

        assertThrows(DataException.class,
                () -> smallFullService.removeLink(COMPANY_ACCOUNTS_ID, smallFullLinkType, request));

        verify(links, times(1)).remove(smallFullLinkType.getLink());
    }

    @Test
    @DisplayName("Tests the  removal of a small full link where the entity is not found")
    void removeLinkSmallFullEntityNotFound() {
        setUpKeyIdGeneratorStubbing();

        SmallFullEntity smallFullEntity = null;
        when(smallFullRepository.findById(GENERATED_ID))
                .thenReturn(Optional.ofNullable(smallFullEntity));

        SmallFullLinkType smallFullLinkType = SmallFullLinkType.TANGIBLE_ASSETS_NOTE;

        assertThrows(DataException.class,
                () -> smallFullService.removeLink(COMPANY_ACCOUNTS_ID, smallFullLinkType, request));

        verify(smallFullRepository, never()).save(smallFullEntity);
    }

    @Test
    @DisplayName("Update small full - success")
    void updateSmallFullSuccess() throws DataException, ServiceException {

        setUpKeyIdGeneratorStubbing();

        when(smallFullRepository.findById(GENERATED_ID)).thenReturn(Optional.of(smallFullEntity));

        when(smallFullEntity.getData()).thenReturn(smallFullDataEntity);

        when(smallFullDataEntity.getLinks()).thenReturn(links);

        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        setUpCompanyProfileApiStubbing();
        when(smallFull.getNextAccounts()).thenReturn(nextAccounts);

        when(smallFullTransformer.transform(smallFull)).thenReturn(smallFullEntity);
        doNothing().when(statementService).invalidateStatementsIfExisting(COMPANY_ACCOUNTS_ID, request);

        ResponseObject<SmallFull> responseObject = smallFullService.update(smallFull, transaction, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.UPDATED, responseObject.getStatus());
        assertNull(responseObject.getData());

        verify(smallFull).setLinks(links);
        verify(smallFull).setKind(Kind.SMALL_FULL_ACCOUNT.getValue());
        verify(smallFull).setEtag(anyString());

        verify(smallFullEntity).setId(GENERATED_ID);
        verify(smallFullRepository).save(smallFullEntity);
    }

    @Test
    @DisplayName("Update small full - not found")
    void updateSmallFullNotFound() {

        setUpKeyIdGeneratorStubbing();

        when(smallFullRepository.findById(GENERATED_ID)).thenReturn(Optional.empty());

        assertThrows(DataException.class, () -> smallFullService.update(smallFull, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Update small full - company profile service exception")
    void updateSmallFullCompanyProfileServiceException() throws ServiceException {

        setUpKeyIdGeneratorStubbing();

        when(smallFullRepository.findById(GENERATED_ID)).thenReturn(Optional.of(smallFullEntity));

        when(smallFullEntity.getData()).thenReturn(smallFullDataEntity);

        when(smallFullDataEntity.getLinks()).thenReturn(links);

        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenThrow(ServiceException.class);

        assertThrows(DataException.class, () -> smallFullService.update(smallFull, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Update small full - MongoException")
    void updateSmallFullMongoException() throws ServiceException {

        setUpKeyIdGeneratorStubbing();

        when(smallFullRepository.findById(GENERATED_ID)).thenReturn(Optional.of(smallFullEntity));

        when(smallFullEntity.getData()).thenReturn(smallFullDataEntity);

        when(smallFullDataEntity.getLinks()).thenReturn(links);

        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        setUpCompanyProfileApiStubbing();
        when(smallFull.getNextAccounts()).thenReturn(nextAccounts);

        when(smallFullTransformer.transform(smallFull)).thenReturn(smallFullEntity);

        doThrow(MongoException.class).when(smallFullRepository).save(smallFullEntity);

        assertThrows(DataException.class, () -> smallFullService.update(smallFull, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    private void setUpKeyIdGeneratorStubbing() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.SMALL_FULL.getName()))
                .thenReturn(GENERATED_ID);
    }

    private void setUpTransactionStubbing() {
        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);
    }

    private void setUpCompanyProfileApiStubbing() {
        when(companyProfileApi.getAccounts()).thenReturn(companyAccountApi);
        when(companyProfileApi.getAccounts().getNextAccounts()).thenReturn(nextAccountsApi);
        when(companyProfileApi.getAccounts().getLastAccounts()).thenReturn(lastAccountsApi);
    }

    private void setUpCompanyProfileApiNoNextAccountsStubbing() {
        when(companyProfileApi.getAccounts()).thenReturn(companyAccountApi);
    }
}
