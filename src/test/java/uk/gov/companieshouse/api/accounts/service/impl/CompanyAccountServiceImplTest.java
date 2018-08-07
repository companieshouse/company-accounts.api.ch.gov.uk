package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.PatchException;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionManager;
import uk.gov.companieshouse.api.accounts.transaction.TransactionStatus;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountServiceImplTest {

    @InjectMocks
    private CompanyAccountServiceImpl companyAccountService;

    @Mock
    private CompanyAccount companyAccountMock;

    @Mock
    private CompanyAccountEntity companyAccountEntityMock;

    @Mock
    private CompanyAccountRepository companyAccountRepository;

    @Mock
    private CompanyAccountTransformer companyAccountTransformer;

    @Mock
    private TransactionManager transactionManagerMock;

    @Test
    @DisplayName("Tests the successful creation of an company account resource")
    void createAccountWithSuccess() {
        doReturn(companyAccountEntityMock).when(companyAccountTransformer).transform(ArgumentMatchers
                .any(CompanyAccount.class));

        ResponseObject response = companyAccountService.createCompanyAccount(companyAccountMock, createDummyTransaction(TransactionStatus.OPEN), anyString());
  
        assertNotNull(response);
        assertNotNull(response.getData());
        verify(companyAccountRepository).insert(companyAccountEntityMock);
    }

    @Test
    @DisplayName("Tests the unsuccessful creation of an company account resource due to duplicate key scenario")
    void createAccountWithDuplicateKeyFailure() {
        doReturn(companyAccountEntityMock).when(companyAccountTransformer).transform(ArgumentMatchers
                .any(CompanyAccount.class));

        when(companyAccountRepository.insert(companyAccountEntityMock)).thenThrow(mock(DuplicateKeyException.class));

        ResponseObject response = companyAccountService.createCompanyAccount(companyAccountMock, createDummyTransaction(TransactionStatus.OPEN), anyString());

        assertNotNull(response);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        verify(companyAccountRepository).insert(companyAccountEntityMock);
    }

    @Test
    @DisplayName("Tests the unsuccessful creation of an company account resource due to mongo error scenario")
    void createAccountWithMongoExceptionFailure() {
        doReturn(companyAccountEntityMock).when(companyAccountTransformer).transform(ArgumentMatchers
                .any(CompanyAccount.class));

        when(companyAccountRepository.insert(companyAccountEntityMock)).thenThrow(mock(MongoException.class));

        ResponseObject response = companyAccountService.createCompanyAccount(companyAccountMock, createDummyTransaction(TransactionStatus.OPEN), anyString());

        assertNotNull(response);
        assertEquals(ResponseStatus.MONGO_ERROR, response.getStatus());
        verify(companyAccountRepository).insert(companyAccountEntityMock);
    }

    @Test
    @DisplayName("Tests the successful creation of an company account resource with a transaction patch failure")
    void createAccountWithTransactionPatchFailure() throws PatchException {
        doReturn(companyAccountEntityMock).when(companyAccountTransformer).transform(ArgumentMatchers
                .any(CompanyAccount.class));

        doThrow(mock(PatchException.class)).when(transactionManagerMock).updateTransaction(anyString(), anyString(), anyString());

        ResponseObject response = companyAccountService.createCompanyAccount(companyAccountMock, createDummyTransaction(TransactionStatus.OPEN), anyString());

        assertNotNull(response);
        assertEquals(ResponseStatus.TRANSACTION_PATCH_ERROR, response.getStatus());
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
        transaction.setStatus(status.getStatus());
        transaction.setLinks(createLinksMap());

        return transaction;
    }

    /**
     * creates an a links map with a test self link
     *
     * @return populated links map
     */
    private Map<String, String> createLinksMap() {
        Map<String, String> links = new HashMap<>();
        links.put(LinkType.SELF.getLink(), "selfLinkTest");
        return links;
    }
}