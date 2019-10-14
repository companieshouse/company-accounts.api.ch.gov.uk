package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StocksControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String INJECTED_CONTROLLER = "baseController";

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Stocks stocks;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private BaseController<Stocks> baseController;

    @InjectMocks
    private StocksController stocksController;

    @Test
    @DisplayName("Create stocks resource")
    void createStocksResource() {

        ReflectionTestUtils.setField(stocksController, INJECTED_CONTROLLER, baseController);

        when(baseController
                .create(stocks, bindingResult, COMPANY_ACCOUNTS_ID, request))
                        .thenReturn(responseEntity);

        assertEquals(responseEntity,
                stocksController
                        .create(stocks, bindingResult, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Update stocks resource")
    void updateStocksResource() {

        ReflectionTestUtils.setField(stocksController, INJECTED_CONTROLLER, baseController);

        when(baseController
                .update(stocks, bindingResult, COMPANY_ACCOUNTS_ID, request))
                        .thenReturn(responseEntity);

        assertEquals(responseEntity,
                stocksController
                        .update(stocks, bindingResult, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Get stocks resource")
    void getStocksResource() {

        ReflectionTestUtils.setField(stocksController, INJECTED_CONTROLLER, baseController);

        when(baseController.get(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseEntity);

        assertEquals(responseEntity,
                stocksController.get(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete stocks resource")
    void deleteStocksResource() {

        ReflectionTestUtils.setField(stocksController, INJECTED_CONTROLLER, baseController);

        when(baseController.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseEntity);

        assertEquals(responseEntity,
                stocksController.delete(COMPANY_ACCOUNTS_ID, request));
    }
}
