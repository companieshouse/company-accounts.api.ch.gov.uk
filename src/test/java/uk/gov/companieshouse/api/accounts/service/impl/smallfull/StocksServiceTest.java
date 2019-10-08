package uk.gov.companieshouse.api.accounts.service.impl.smallfull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.stocks.StocksEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;
import uk.gov.companieshouse.api.accounts.service.impl.BaseService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StocksServiceTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";

    private static final String EXPECTED_SELF_LINK = TRANSACTION_SELF_LINK + "/company-accounts/" +
            COMPANY_ACCOUNTS_ID + "/small-full/notes/stocks";

    private static final String INJECTED_SERVICE = "baseService";

    @Mock
    private Stocks stocks;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks links;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ResponseObject<Stocks> responseObject;

    @Mock
    private BaseService<Stocks, StocksEntity, SmallFullLinkType> baseService;

    @InjectMocks
    private StocksService stocksService;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(stocksService, INJECTED_SERVICE, baseService);
    }

    @Test
    @DisplayName("Create stocks resource")
    void createStocksResource() throws DataException {

        when(transaction.getLinks()).thenReturn(links);
        when(links.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(baseService
                .create(stocks, transaction, COMPANY_ACCOUNTS_ID, request, EXPECTED_SELF_LINK))
                        .thenReturn(responseObject);

        assertEquals(responseObject,
                stocksService
                        .create(stocks, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Update stocks resource")
    void updateStocksResource() throws DataException {

        when(transaction.getLinks()).thenReturn(links);
        when(links.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(baseService
                .update(stocks, transaction, COMPANY_ACCOUNTS_ID, request, EXPECTED_SELF_LINK))
                        .thenReturn(responseObject);

        assertEquals(responseObject,
                stocksService
                        .update(stocks, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Get stocks resource")
    void getStocksResource() throws DataException {

        when(baseService.find(COMPANY_ACCOUNTS_ID)).thenReturn(responseObject);

        assertEquals(responseObject,
                stocksService.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete stocks resource")
    void deleteStocksResource() throws DataException {

        when(baseService.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        assertEquals(responseObject,
                stocksService.delete(COMPANY_ACCOUNTS_ID, request));
    }
}
