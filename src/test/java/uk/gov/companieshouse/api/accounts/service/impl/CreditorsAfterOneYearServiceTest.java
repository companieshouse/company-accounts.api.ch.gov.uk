package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CreditorsAfterOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CreditorsAfterOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CreditorsAfterOneYear;
import uk.gov.companieshouse.api.accounts.repository.CreditorsAfterOneYearRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CreditorsAfterOneYearTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreditorsAfterOneYearServiceTest {

    @InjectMocks
    private CreditorsAfterOneYearService mockCreditorsAfterOneYearService;

    @Mock
    private CreditorsAfterOneYearTransformer mockTransformer;

    @Mock
    private CreditorsAfterOneYear mockCreditorsAfterOneYear;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CreditorsAfterOneYearRepository mockRepository;

    @Mock
    private DuplicateKeyException mockDuplicateKeyException;

    @Mock
    private KeyIdGenerator mockKeyIdGenerator;

    @Mock
    private MongoException mockMongoException;

    @Mock
    private SmallFullService mockSmallFullService;

    private CreditorsAfterOneYearEntity creditorsAfterOneYearEntity;


    @BeforeEach
    void setUp() {

        CreditorsAfterOneYearDataEntity dataEntity = new CreditorsAfterOneYearDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), "self_link");
        dataEntity.setLinks(links);

        creditorsAfterOneYearEntity = new CreditorsAfterOneYearEntity();
        creditorsAfterOneYearEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of a creditors after one year resource")
    void canCreateCreditorsAfterOneYear() throws DataException {

        when(mockTransformer.transform(mockCreditorsAfterOneYear)).thenReturn(creditorsAfterOneYearEntity);

        ResponseObject<CreditorsAfterOneYear> result =
                mockCreditorsAfterOneYearService.create(mockCreditorsAfterOneYear, mockTransaction,
                "", mockRequest);

        assertNotNull(result);
        assertEquals(ResponseStatus.CREATED, result.getStatus());
        assertEquals(mockCreditorsAfterOneYear, result.getData());
        verify(mockSmallFullService).addLink(anyString(), any(SmallFullLinkType.class),
                anyString(), any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a creditors after one year resource")
    void createCreditorsAfterOneYearDuplicateKey() throws DataException {

        doReturn(creditorsAfterOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
                .any(CreditorsAfterOneYear.class));
        when(mockRepository.insert(creditorsAfterOneYearEntity)).thenThrow(mockDuplicateKeyException);

        ResponseObject<CreditorsAfterOneYear> result =
                mockCreditorsAfterOneYearService.create(mockCreditorsAfterOneYear,
                        mockTransaction, "", mockRequest);

        assertNotNull(result);
        assertEquals(result.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating creditors after one year")
    void createCreditorsAfterOneYearMongoExceptionFailure() {

        doReturn(creditorsAfterOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
                .any(CreditorsAfterOneYear.class));
        when(mockRepository.insert(creditorsAfterOneYearEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
                () -> mockCreditorsAfterOneYearService.create(mockCreditorsAfterOneYear,
                        mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful update of a creditors after one year resource")
    void canUpdateACreditorsAfterOneYear() throws DataException {

        when(mockTransformer.transform(mockCreditorsAfterOneYear)).thenReturn(creditorsAfterOneYearEntity);

        ResponseObject<CreditorsAfterOneYear> result = mockCreditorsAfterOneYearService.update(mockCreditorsAfterOneYear, mockTransaction,
                "", mockRequest);

        assertNotNull(result);
        assertEquals(mockCreditorsAfterOneYear, result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating a creditors after one year")
    void updateCreditorsAfterOneYearMongoExceptionFailure() throws DataException {

        doReturn(creditorsAfterOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
                .any(CreditorsAfterOneYear.class));
        when(mockRepository.save(creditorsAfterOneYearEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
                () -> mockCreditorsAfterOneYearService.update(mockCreditorsAfterOneYear, mockTransaction, "", mockRequest));
    }

}
