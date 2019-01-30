package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
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
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.stocks.StocksDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.stocks.StocksEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;
import uk.gov.companieshouse.api.accounts.repository.StocksRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.StocksTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StocksServiceTest {

    @InjectMocks
    private StocksService service;

    @Mock
    private StocksTransformer mockTransformer;

    @Mock
    private Stocks mockStocks;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private StocksRepository mockRepository;

    @Mock
    private DuplicateKeyException mockDuplicateKeyException;

    @Mock
    private MongoException mockMongoException;

    @Mock
    private SmallFullService mockSmallFullService;

    @Mock
    private KeyIdGenerator mockKeyIdGenerator;

    private StocksEntity stocksEntity;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";

    @BeforeEach
    void setUp() {

        StocksDataEntity dataEntity = new StocksDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), "self_link");
        dataEntity.setLinks(links);

        stocksEntity = new StocksEntity();
        stocksEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of a stocks resource")
    void canCreateStocks() throws DataException {

        when(mockTransformer.transform(mockStocks)).thenReturn(stocksEntity);

        ResponseObject<Stocks> result = service.create(mockStocks, mockTransaction,
                "", mockRequest);

        assertNotNull(result);
        assertEquals(ResponseStatus.CREATED, result.getStatus());
        assertEquals(mockStocks, result.getData());
        verify(mockSmallFullService).addLink(anyString(), any(SmallFullLinkType.class), anyString(), any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a stocks resource")
    void createStocksDuplicateKey() throws DataException {

        doReturn(stocksEntity).when(mockTransformer).transform(ArgumentMatchers.any(Stocks.class));
        when(mockRepository.insert(stocksEntity)).thenThrow(mockDuplicateKeyException);

        ResponseObject<Stocks> result = service.create(mockStocks, mockTransaction, "", mockRequest);

        assertNotNull(result);
        assertEquals(result.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating stocks")
    void createStocksMongoExceptionFailure() {

        doReturn(stocksEntity).when(mockTransformer).transform(ArgumentMatchers
                .any(Stocks.class));
        when(mockRepository.insert(stocksEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
                () -> service.create(mockStocks, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful update of a stocks resource")
    void canUpdateStocks() throws DataException {

        when(mockTransformer.transform(mockStocks)).thenReturn(stocksEntity);

        ResponseObject<Stocks> result = service.update(mockStocks, mockTransaction,
                "", mockRequest);

        assertNotNull(result);
        assertEquals(mockStocks, result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating a stocks resource")
    void updateStocksMongoExceptionFailure() {

        doReturn(stocksEntity).when(mockTransformer).transform(ArgumentMatchers
                .any(Stocks.class));
        when(mockRepository.save(stocksEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
                () -> service.update(mockStocks, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful find of a stocks resource")
    void findStocks() throws DataException {

        when(mockRepository.findById(""))
                .thenReturn(Optional.ofNullable(stocksEntity));
        when(mockTransformer.transform(stocksEntity)).thenReturn(mockStocks);

        ResponseObject<Stocks> result = service.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(mockStocks, result.getData());
    }

    @Test
    @DisplayName("Tests stocks response not found")
    void findStocksResponseNotFound() throws DataException {

        stocksEntity = null;

        when(mockRepository.findById(""))
                .thenReturn(Optional.ofNullable(stocksEntity));

        ResponseObject<Stocks> result = service.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(responseStatusNotFound(), result.getStatus());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a stocks resource")
    void findStocksMongoException() {

        when(mockRepository.findById("")).thenThrow(mockMongoException);
        assertThrows(DataException.class, () -> service.findById("", mockRequest));
    }

    @Test
    @DisplayName("Test the successful delete of a stocks resource")
    void deleteStocks() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STOCKS.getName()))
                .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(true);
        doNothing().when(mockRepository).deleteById(GENERATED_ID);

        ResponseObject<Stocks> responseObject = service.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.UPDATED);
        verify(mockSmallFullService, times(1))
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.STOCKS_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Test attempt to delete empty resource produces not found response")
    void deleteEmptyStocks() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STOCKS.getName()))
                .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(false);
        ResponseObject<Stocks> responseObject = service.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.NOT_FOUND);
        verify(mockSmallFullService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.STOCKS_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Tests mongo exception thrown on deletion of a stocks resource")
    void deleteStocksMongoException() {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.STOCKS.getName()))
                .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(true);
        doThrow(mockMongoException).when(mockRepository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () -> service.delete(COMPANY_ACCOUNTS_ID, mockRequest));
    }

    private ResponseStatus responseStatusNotFound() {
        ResponseObject responseObject = new ResponseObject<>(ResponseStatus.NOT_FOUND);
        return responseObject.getStatus();
    }
}
