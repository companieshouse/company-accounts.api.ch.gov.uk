package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.PayableResource;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.sdk.ApiClientService;
import uk.gov.companieshouse.api.accounts.service.TransactionService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.privatetransaction.PrivateTransactionResourceHandler;
import uk.gov.companieshouse.api.handler.privatetransaction.request.PrivateTransactionPatch;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CompanyAccountServiceImplTest {
    @Mock
    private CompanyAccountRepository repository;

    @Mock
    private CompanyAccountTransformer transformer;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private CompanyAccountServiceImpl companyAccountService;

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private CompanyAccountEntity companyAccountEntity;

    @Mock
    private CompanyAccountDataEntity companyAccountDataEntity;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateTransactionResourceHandler privateTransactionResourceHandler;

    @Mock
    private PrivateTransactionPatch privateTransactionPatch;

    @Mock
    private Map<String, String> companyAccountsLinks;

    private static final String TRANSACTION_ID = "transactionId";

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String TRANSACTION_SELF_LINK = "transaction_self_link";

    private static final String ERIC_PASSTHROUGH_TOKEN_HEADER = "ERIC-Access-Token";

    private static final String ERIC_PASSTHROUGH_TOKEN = "ericPassthroughToken";

    private static final String TRANSACTION_PATCH_LINK = "/private/transactions/" + TRANSACTION_ID;

    @Test
    @DisplayName("Create company accounts - success path with next and last accounts")
    void createCompanyAccountsSuccessPathWithNextAndLastAccounts()
            throws ServiceException, IOException, PatchException, DataException {
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(companyAccount)).thenReturn(companyAccountEntity);

        when(request.getHeader(ERIC_PASSTHROUGH_TOKEN_HEADER)).thenReturn(ERIC_PASSTHROUGH_TOKEN);

        when(apiClientService.getInternalApiClient(ERIC_PASSTHROUGH_TOKEN)).thenReturn(internalApiClient);

        when(transaction.getId()).thenReturn(TRANSACTION_ID);

        when(internalApiClient.privateTransaction()).thenReturn(privateTransactionResourceHandler);
        when(privateTransactionResourceHandler.patch(TRANSACTION_PATCH_LINK, transaction))
                .thenReturn(privateTransactionPatch);

        when(transactionService.getPayableResources(transaction)).thenReturn(List.of(PayableResource.CIC));

        ResponseObject<CompanyAccount> response = companyAccountService.create(companyAccount, transaction, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(companyAccount, response.getData());
        assertNull(response.getErrors());

        verify(companyAccount, times(1)).setEtag(anyString());
        verify(companyAccount, times(1)).setLinks(anyMap());
        verify(companyAccount, times(1)).setKind(Kind.COMPANY_ACCOUNTS.getValue());

        verify(companyAccountEntity, times(1)).setId(anyString());

        verify(repository, times(1)).insert(companyAccountEntity);

        verify(transaction, times(1)).setResources(anyMap());
    }

    @Test
    @DisplayName("Create company accounts - success path with only next accounts")
    void createCompanyAccountsSuccessPathWithNextAccounts()
            throws ServiceException, IOException, PatchException, DataException {
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(companyAccount)).thenReturn(companyAccountEntity);

        when(request.getHeader(ERIC_PASSTHROUGH_TOKEN_HEADER)).thenReturn(ERIC_PASSTHROUGH_TOKEN);

        when(apiClientService.getInternalApiClient(ERIC_PASSTHROUGH_TOKEN)).thenReturn(internalApiClient);

        when(transaction.getId()).thenReturn(TRANSACTION_ID);

        when(internalApiClient.privateTransaction()).thenReturn(privateTransactionResourceHandler);
        when(privateTransactionResourceHandler.patch(TRANSACTION_PATCH_LINK, transaction))
                .thenReturn(privateTransactionPatch);

        when(transactionService.getPayableResources(transaction)).thenReturn(new ArrayList<>());

        ResponseObject<CompanyAccount> response = companyAccountService.create(companyAccount, transaction, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(companyAccount, response.getData());
        assertNull(response.getErrors());

        verify(companyAccount, times(1)).setEtag(anyString());
        verify(companyAccount, times(1)).setLinks(anyMap());
        verify(companyAccount, times(1)).setKind(Kind.COMPANY_ACCOUNTS.getValue());

        verify(companyAccountEntity, times(1)).setId(anyString());

        verify(repository, times(1)).insert(companyAccountEntity);

        verify(transaction, times(1)).setResources(anyMap());
    }

    @Test
    @DisplayName("Create company accounts - duplicate key exception")
    void createCompanyAccountsDuplicateKeyException() throws PatchException, DataException {
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(companyAccount)).thenReturn(companyAccountEntity);

        when(repository.insert(companyAccountEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<CompanyAccount> response = companyAccountService.create(companyAccount, transaction, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Create company accounts - Mongo exception")
    void createCompanyAccountsMongoException() {
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(companyAccount)).thenReturn(companyAccountEntity);

        when(repository.insert(companyAccountEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> companyAccountService.create(companyAccount, transaction, request));
    }

    @Test
    @DisplayName("Create company accounts - IO exception")
    void createCompanyAccountsIOException() throws IOException {
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(companyAccount)).thenReturn(companyAccountEntity);

        when(request.getHeader(ERIC_PASSTHROUGH_TOKEN_HEADER)).thenReturn(ERIC_PASSTHROUGH_TOKEN);

        when(apiClientService.getInternalApiClient(ERIC_PASSTHROUGH_TOKEN)).thenThrow(IOException.class);

        assertThrows(PatchException.class, () -> companyAccountService.create(companyAccount, transaction, request));
    }

    @Test
    @DisplayName("Create company accounts - URI validation exception")
    void createCompanyAccountsURIValidationException() throws ServiceException, IOException, URIValidationException {
        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(companyAccount)).thenReturn(companyAccountEntity);

        when(request.getHeader(ERIC_PASSTHROUGH_TOKEN_HEADER)).thenReturn(ERIC_PASSTHROUGH_TOKEN);

        when(apiClientService.getInternalApiClient(ERIC_PASSTHROUGH_TOKEN)).thenReturn(internalApiClient);

        when(transaction.getId()).thenReturn(TRANSACTION_ID);

        when(internalApiClient.privateTransaction()).thenReturn(privateTransactionResourceHandler);
        when(privateTransactionResourceHandler.patch(TRANSACTION_PATCH_LINK, transaction))
                .thenReturn(privateTransactionPatch);

        when(transactionService.getPayableResources(transaction)).thenReturn(new ArrayList<>());

        when(privateTransactionPatch.execute()).thenThrow(URIValidationException.class);

        assertThrows(PatchException.class, () -> companyAccountService.create(companyAccount, transaction, request));
    }

    @Test
    @DisplayName("Find company accounts - success path")
    void findCompanyAccountsSuccessPath() throws DataException {
        when(repository.findById(COMPANY_ACCOUNTS_ID)).thenReturn(Optional.of(companyAccountEntity));

        when(transformer.transform(companyAccountEntity)).thenReturn(companyAccount);

        ResponseObject<CompanyAccount> response = companyAccountService.findById(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(companyAccount, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Find company accounts - not found")
    void findCompanyAccountsNotFound() throws DataException {
        when(repository.findById(COMPANY_ACCOUNTS_ID)).thenReturn(Optional.empty());

        ResponseObject<CompanyAccount> response = companyAccountService.findById(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Find company accounts - Mongo exception")
    void findCompanyAccountsMongoException() {
        when(repository.findById(COMPANY_ACCOUNTS_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> companyAccountService.findById(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Add link - success path")
    void addLinkSuccessPath() {
        when(repository.findById(COMPANY_ACCOUNTS_ID)).thenReturn(Optional.of(companyAccountEntity));

        when(companyAccountEntity.getData()).thenReturn(companyAccountDataEntity);

        companyAccountService.addLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.TRANSACTION, TRANSACTION_SELF_LINK);

        verify(companyAccountDataEntity, times(1)).setLinks(anyMap());

        verify(repository, times(1)).save(companyAccountEntity);
    }

    @Test
    @DisplayName("Add link - company account not found")
    void addLinkCompanyAccountNotFound() {
        when(repository.findById(COMPANY_ACCOUNTS_ID)).thenReturn(Optional.empty());

        assertThrows(MongoException.class, () -> companyAccountService.addLink(
                COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.TRANSACTION, TRANSACTION_SELF_LINK));
    }

    @Test
    @DisplayName("Remove link - success path")
    void removeLinkSuccessPath() {
        when(repository.findById(COMPANY_ACCOUNTS_ID)).thenReturn(Optional.of(companyAccountEntity));

        when(companyAccountEntity.getData()).thenReturn(companyAccountDataEntity);

        when(companyAccountDataEntity.getLinks()).thenReturn(companyAccountsLinks);

        CompanyAccountLinkType linkType = CompanyAccountLinkType.CIC_REPORT;

        assertAll(() -> companyAccountService.removeLink(COMPANY_ACCOUNTS_ID, linkType));

        verify(companyAccountsLinks, times(1)).remove(linkType.getLink());
        verify(repository, times(1)).save(companyAccountEntity);
    }

    @Test
    @DisplayName("Remove link - company account not found")
    void removeLinkCompanyAccountNotFound() {
        when(repository.findById(COMPANY_ACCOUNTS_ID)).thenReturn(Optional.empty());

        CompanyAccountLinkType linkType = CompanyAccountLinkType.CIC_REPORT;

        assertThrows(MongoException.class, () -> companyAccountService.removeLink(COMPANY_ACCOUNTS_ID, linkType));
    }
}