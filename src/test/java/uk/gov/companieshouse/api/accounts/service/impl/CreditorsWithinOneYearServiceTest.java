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
import uk.gov.companieshouse.api.accounts.model.entity.notes.CreditorsWithinOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.CreditorsWithinOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.repository.CreditorsWithinOneYearRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CreditorsWithinOneYearTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreditorsWithinOneYearServiceTest {

    @InjectMocks
    private CreditorsWithinOneYearService service;

    @Mock
    private CreditorsWithinOneYearTransformer mockTransformer;

    @Mock
    private CreditorsWithinOneYear mockCreditorsWithinOneYear;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CreditorsWithinOneYearRepository mockRepository;

    @Mock
    private DuplicateKeyException mockDuplicateKeyException;

    @Mock
    private MongoException mockMongoException;

    @Mock
    private SmallFullService mockSmallFullService;

    @Mock
    private KeyIdGenerator mockKeyIdGenerator;

    private CreditorsWithinOneYearEntity creditorsWithinOneYearEntity;


    @BeforeEach
    void setUp() {

        CreditorsWithinOneYearDataEntity dataEntity = new CreditorsWithinOneYearDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), "self_link");
        dataEntity.setLinks(links);

        creditorsWithinOneYearEntity = new CreditorsWithinOneYearEntity();
        creditorsWithinOneYearEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of a Creditors within one year resource")
    void canCreateCreditorsWithinOneYear() throws DataException {

        when(mockTransformer.transform(mockCreditorsWithinOneYear)).thenReturn(creditorsWithinOneYearEntity);

        ResponseObject<CreditorsWithinOneYear> result = service.create(mockCreditorsWithinOneYear, mockTransaction,
            "", mockRequest);

        assertNotNull(result);
        assertEquals(ResponseStatus.CREATED, result.getStatus());
        assertEquals(mockCreditorsWithinOneYear, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a Creditors within one year resource")
    void createCreditorsWithinOneYearDuplicateKey() throws DataException {

        doReturn(creditorsWithinOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(CreditorsWithinOneYear.class));
        when(mockRepository.insert(creditorsWithinOneYearEntity)).thenThrow(mockDuplicateKeyException);

        ResponseObject<CreditorsWithinOneYear> result = service.create(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest);

        assertNotNull(result);
        assertEquals(result.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating Creditors within one year")
    void createCreditorsWithinOneYearMongoExceptionFailure() {

        doReturn(creditorsWithinOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(CreditorsWithinOneYear.class));
        when(mockRepository.insert(creditorsWithinOneYearEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
            () -> service.create(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest));
    }
}
