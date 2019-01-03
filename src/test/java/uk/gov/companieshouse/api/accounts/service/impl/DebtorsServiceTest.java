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
import java.util.Optional;
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
    private DebtorsTransformer mockTransformer;

    @Mock
    private Debtors mockDebtors;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private DebtorsRepository mockRepository;

    @Mock
    private DebtorsValidator debtorsValidator;

    @Mock
    private SmallFullService mockSmallFullService;

    @Mock
    private DuplicateKeyException mockDuplicateKeyException;

    @Mock
    private MongoException mockMongoException;

    @Mock
    private KeyIdGenerator mockKeyIdGenerator;

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

        when(mockTransformer.transform(mockDebtors)).thenReturn(debtorsEntity);
        when(debtorsValidator.validateDebtors(mockDebtors, mockTransaction, "",mockRequest)).thenReturn(errors);


        ResponseObject<Debtors> result = service.create(mockDebtors, mockTransaction,
            "", mockRequest);

        assertNotNull(result);
        assertEquals(mockDebtors, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a Debtors resource")
    void createDebtorsDuplicateKey() throws DataException {

        doReturn(debtorsEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(Debtors.class));

        when(debtorsValidator.validateDebtors(mockDebtors, mockTransaction, "",mockRequest)).thenReturn(errors);
        when(mockRepository.insert(debtorsEntity)).thenThrow(mockDuplicateKeyException);

        ResponseObject response = service.create(mockDebtors, mockTransaction, "",
            mockRequest);

        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating Debtors")
    void createDebtorsMongoExceptionFailure() throws DataException {

        doReturn(debtorsEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(Debtors.class));
        when(debtorsValidator.validateDebtors(mockDebtors, mockTransaction, "",mockRequest)).thenReturn(errors);
        when(mockRepository.insert(debtorsEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
            () -> service.create(mockDebtors, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful update of an Debtors resource")
    void canUpdateADebtors() throws DataException {

        when(mockTransformer.transform(mockDebtors)).thenReturn(debtorsEntity);

        ResponseObject<Debtors> result = service.update(mockDebtors, mockTransaction,
            "", mockRequest);

        assertNotNull(result);
        assertEquals(mockDebtors, result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating an Debtors")
    void updateDebtorsMongoExceptionFailure() {

        doReturn(debtorsEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(Debtors.class));
        when(mockRepository.save(debtorsEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
            () -> service.update(mockDebtors, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful find of an Debtors resource")
    void findDebtors() throws DataException {

        when(mockRepository.findById(""))
            .thenReturn(Optional.ofNullable(debtorsEntity));
        when(mockTransformer.transform(debtorsEntity)).thenReturn(mockDebtors);

        ResponseObject<Debtors> result = service.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(mockDebtors, result.getData());
    }

    @Test
    @DisplayName("Tests Debtors response not found")
    void findDebtorsResponseNotFound() throws DataException {
        debtorsEntity = null;
        when(mockRepository.findById(""))
            .thenReturn(Optional.ofNullable(debtorsEntity));

        ResponseObject<Debtors> result = service.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(responseStatusNotFound(), result.getStatus());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of an Debtors resource")
    void findDebtorsMongoException() {
        when(mockRepository.findById("")).thenThrow(mockMongoException);

        assertThrows(DataException.class, () -> service.findById("", mockRequest));
    }

    private ResponseStatus responseStatusNotFound() {
        ResponseObject responseObject = new ResponseObject<>(ResponseStatus.NOT_FOUND);
        return responseObject.getStatus();
    }
}
