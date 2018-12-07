package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.DebtorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.DebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.DebtorsRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.DebtorsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.DebtorsValidator;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DebtorsServiceTest {

    @Mock
    private DebtorsTransformer transformer;

    @Mock
    private Debtors debtors;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private DebtorsRepository repository;

    @Mock
    private DebtorsValidator debtorsValidator;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private Errors errors;

    @InjectMocks
    private DebtorsService service;

    private DebtorsEntity debtorsEntity;

    @BeforeEach
    void setUp() {

        DebtorsDataEntity dataEntity = new DebtorsDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), "self_link");
        dataEntity.setLinks(links);

        debtorsEntity = new DebtorsEntity();
        debtorsEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of a debtors resource")
    void canCreateDebtors() throws DataException {

        errors = new Errors();

        when(transformer.transform(debtors)).thenReturn(debtorsEntity);
        when(debtorsValidator.validateDebtors(debtors,transaction)).thenReturn(errors);

        ResponseObject<Debtors> result = service.create(debtors, transaction,
            "", request);

        assertNotNull(result);
        assertEquals(debtors, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a Debtors resource")
    void createDebtorsDuplicateKey() throws DataException {

        doReturn(debtorsEntity).when(transformer).transform(ArgumentMatchers
            .any(Debtors.class));
        when(debtorsValidator.validateDebtors(debtors,transaction)).thenReturn(errors);
        when(repository.insert(debtorsEntity)).thenThrow(duplicateKeyException);

        ResponseObject response = service.create(debtors, transaction, "", request);

        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating Debtors")
    void createDebtorsMongoExceptionFailure() {

        doReturn(debtorsEntity).when(transformer).transform(ArgumentMatchers
            .any(Debtors.class));
        when(debtorsValidator.validateDebtors(debtors,transaction)).thenReturn(errors);
        when(repository.insert(debtorsEntity)).thenThrow(mongoException);

        assertThrows(DataException.class,
            () -> service.create(debtors, transaction, "", request));
    }
}
