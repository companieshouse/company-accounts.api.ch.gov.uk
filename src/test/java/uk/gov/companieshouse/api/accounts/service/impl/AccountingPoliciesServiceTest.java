package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPolicies;
import uk.gov.companieshouse.api.accounts.repository.AccountingPoliciesRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.AccountingPoliciesTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountingPoliciesServiceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private AccountingPolicies accountingPolicies;

    @Mock
    private Transaction transaction;

    @Mock
    private AccountingPoliciesRepository repository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private AccountingPoliciesTransformer transformer;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private AccountingPoliciesService service;

    private AccountingPoliciesEntity accountingPoliciesEntity;

    @BeforeEach
    public void setUp() {

        AccountingPoliciesDataEntity dataEntity = new AccountingPoliciesDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), "self_link");
        dataEntity.setLinks(links);

        accountingPoliciesEntity = new AccountingPoliciesEntity();
        accountingPoliciesEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of an AccountingPolicies resource")
    public void canCreateAnAccountingPolicies() throws DataException {

        when(transformer.transform(accountingPolicies)).thenReturn(accountingPoliciesEntity);

        ResponseObject<AccountingPolicies> result = service.create(accountingPolicies, transaction,
                                                "", request);

        assertNotNull(result);
        assertEquals(accountingPolicies, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating an AccountingPolicies resource")
    public void createAccountingPoliciesDuplicateKey() throws DataException {

        doReturn(accountingPoliciesEntity).when(transformer).transform(ArgumentMatchers
                .any(AccountingPolicies.class));
        when(repository.insert(accountingPoliciesEntity)).thenThrow(duplicateKeyException);

        ResponseObject response = service.create(accountingPolicies, transaction, "", request);

        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating an AccountingPolicies")
    void createAccountingPoliciesMongoExceptionFailure() throws DataException {

        doReturn(accountingPoliciesEntity).when(transformer).transform(ArgumentMatchers
                .any(AccountingPolicies.class));
        when(repository.insert(accountingPoliciesEntity)).thenThrow(mongoException);

        assertThrows(DataException.class,
                () -> service.create(accountingPolicies, transaction, "", request));
    }

    @Test
    @DisplayName("Tests the successful update of an AccountingPolicies resource")
    public void canUpdateAnAccountingPolicies() throws DataException {

        when(transformer.transform(accountingPolicies)).thenReturn(accountingPoliciesEntity);

        ResponseObject<AccountingPolicies> result = service.update(accountingPolicies, transaction,
                "", request);

        assertNotNull(result);
        assertEquals(accountingPolicies, result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating an AccountingPolicies")
    void updateAccountingPoliciesMongoExceptionFailure() throws DataException {

        doReturn(accountingPoliciesEntity).when(transformer).transform(ArgumentMatchers
                .any(AccountingPolicies.class));
        when(repository.save(accountingPoliciesEntity)).thenThrow(mongoException);

        assertThrows(DataException.class,
                () -> service.update(accountingPolicies, transaction, "", request));
    }

    @Test
    @DisplayName("Tests the successful find of an AccountingPolicies resource")
    public void findAccountingPolicies() throws DataException {

        when(repository.findById(""))
                .thenReturn(Optional.ofNullable(accountingPoliciesEntity));
        when(transformer.transform(accountingPoliciesEntity)).thenReturn(accountingPolicies);

        ResponseObject<AccountingPolicies> result = service.findById("", request);

        assertNotNull(result);
        assertEquals(accountingPolicies, result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of an AccountingPolicies resource")
    public void findAccountingPoliciesMongoException() throws DataException {
        when(repository.findById("")).thenThrow(mongoException);

        assertThrows(DataException.class, () -> service.findById("", request));
    }
}
