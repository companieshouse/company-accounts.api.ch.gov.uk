package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.sdk.ApiClientService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.privatetransaction.PrivateTransactionResourceHandler;
import uk.gov.companieshouse.api.handler.privatetransaction.request.PrivateTransactionPatch;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountServiceImplTest {

    private static final String SELF_LINK = "self";
    private static final String TRANSACTION_LINK = "transaction";
    private static final String MOCK_TRANSACTION_SELF_LINK = "selfLinkTest";

    @Mock
    private ApiClientService mockApiClientService;

    @Mock
    private InternalApiClient mockApiClient;

    @Mock
    private PrivateTransactionResourceHandler mockTransactionResourceHandler;

    @Mock
    private PrivateTransactionPatch mockTransactionPatch;

    @InjectMocks
    private CompanyAccountServiceImpl companyAccountService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CompanyAccount companyAccountMock;

    @Mock
    private CompanyAccountEntity companyAccountEntityMock;

    @Mock
    private CompanyAccountRepository companyAccountRepository;

    @Mock
    private CompanyAccountTransformer companyAccountTransformer;

    @Test
    @DisplayName("Tests the successful creation of an company account resource")
    void createAccountWithSuccess() throws DataException, PatchException, IOException {
        doReturn(companyAccountEntityMock).when(companyAccountTransformer).transform(any(CompanyAccount.class));

        CompanyAccount companyAccount = new CompanyAccount();

        when(mockApiClientService.getInternalApiClient(anyString())).thenReturn(mockApiClient);
        when(mockApiClient.privateTransaction()).thenReturn(mockTransactionResourceHandler);
        when(mockTransactionResourceHandler.patch(anyString(), any(Transaction.class))).thenReturn(mockTransactionPatch);
        when(request.getHeader("ERIC-Access-Token")).thenReturn("1111");

        ResponseObject response = companyAccountService.create(companyAccount, createDummyTransaction(TransactionStatus.OPEN), request);

        assertNotNull(response);
        assertNotNull(response.getData());

        ArgumentCaptor<CompanyAccount> companyAccountArgument = ArgumentCaptor.forClass(CompanyAccount.class);
        verify(companyAccountTransformer).transform(companyAccountArgument.capture());

        assertNotNull(companyAccountArgument.getValue().getLinks().get(SELF_LINK));
        assertNotNull(companyAccountArgument.getValue().getLinks().get(TRANSACTION_LINK));
        assertEquals(MOCK_TRANSACTION_SELF_LINK, companyAccountArgument.getValue().getLinks().get(TRANSACTION_LINK));

        verify(companyAccountRepository).insert(companyAccountEntityMock);
    }

    @Test
    @DisplayName("Tests the unsuccessful creation of an company account resource due to duplicate key scenario")
    void createAccountWithDuplicateKeyFailure() throws DataException, PatchException {
        doReturn(companyAccountEntityMock).when(companyAccountTransformer).transform(any(CompanyAccount.class));

        when(companyAccountRepository.insert(companyAccountEntityMock)).thenThrow(mock(DuplicateKeyException.class));

        ResponseObject response = companyAccountService.create(companyAccountMock, createDummyTransaction(TransactionStatus.OPEN), request);

        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
        verify(companyAccountRepository).insert(companyAccountEntityMock);
    }

    @Test
    @DisplayName("Tests the unsuccessful creation of an company account resource due to mongo error scenario")
    void createAccountWithMongoExceptionFailure() throws DataException {
        doReturn(companyAccountEntityMock).when(companyAccountTransformer).transform(any(CompanyAccount.class));

        when(companyAccountRepository.insert(companyAccountEntityMock)).thenThrow(mock(MongoException.class));

        Executable executable = ()->{companyAccountService.create(companyAccountMock, createDummyTransaction(TransactionStatus.OPEN), request);};

        assertThrows(DataException.class, executable);

        verify(companyAccountRepository).insert(companyAccountEntityMock);
    }

    @Test
    @DisplayName("Tests the successful creation of an company account resource with a transaction patch failure")
    void createAccountWithTransactionPatchFailure()
            throws IOException, URIValidationException {
        when(request.getHeader(anyString())).thenReturn("");
        doReturn(companyAccountEntityMock).when(companyAccountTransformer).transform(any(CompanyAccount.class));

        when(mockApiClientService.getInternalApiClient(anyString())).thenReturn(mockApiClient);
        when(mockApiClient.privateTransaction()).thenReturn(mockTransactionResourceHandler);
        when(mockTransactionResourceHandler.patch(anyString(), any(Transaction.class))).thenReturn(mockTransactionPatch);
        when(mockTransactionPatch.execute()).thenThrow(new URIValidationException("uri is wrong"));
        when(request.getHeader("ERIC-Access-Token")).thenReturn("1111");

        Executable executable = ()->{companyAccountService.create(companyAccountMock, createDummyTransaction(TransactionStatus.OPEN), request);};

        assertThrows(PatchException.class, executable);

        verify(companyAccountRepository).insert(companyAccountEntityMock);
    }

    /**
     * creates an open or closed dummy transaction depending on the status passed into method
     *
     * @param status - transaction status
     * @return Transaction object with the desired transaction
     */
    private Transaction createDummyTransaction(TransactionStatus status) {
        Transaction transaction = new Transaction();
        transaction.setId("id");
        transaction.setStatus(status);
        transaction.setLinks(createLinks());

        return transaction;
    }

    private TransactionLinks createLinks() {
        TransactionLinks links = new TransactionLinks();
        links.setSelf(MOCK_TRANSACTION_SELF_LINK);
        return links;
    }
}