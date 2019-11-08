package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.stocks.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.stocks.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.stocks.StocksDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.stocks.StocksEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.stocks.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.stocks.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.stocks.Stocks;

import java.util.HashMap;
import uk.gov.companieshouse.api.accounts.transformer.smallfull.StocksTransformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StocksTransformerTest {

    private static final Long PAYMENTS_ON_ACCOUNT_CURRENT_PERIOD = 1L;
    private static final Long STOCKS_CURRENT_PERIOD = 2L;
    private static final Long TOTAL_CURRENT_PERIOD = 3L;

    private static final Long PAYMENTS_ON_ACCOUNT_PREVIOUS_PERIOD = 10L;
    private static final Long STOCKS_PREVIOUS_PERIOD = 20L;
    private static final Long TOTAL_PREVIOUS_PERIOD = 30L;

    private static final String ETAG = "etag";
    private static final String KIND = "kind";

    private StocksTransformer stocksTransformer = new StocksTransformer();

    @Test
    @DisplayName("Tests transformer with empty rest object returns null values ")
    public void testTransformerWithEmptyRestObject() {

        StocksEntity stocksEntity = stocksTransformer
                .transform(new Stocks());

        assertNotNull(stocksEntity);
        assertNull(stocksEntity.getData().getEtag());
        assertEquals(new HashMap<>(), stocksEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with empty previous period Rest Object")
    public void testRestToEntityTransformerWithEmptyPreviousPeriodRestObject() {

        Stocks stocks = new Stocks();

        stocks.setEtag(ETAG);
        stocks.setKind(KIND);
        stocks.setLinks(new HashMap<>());
        stocks.setCurrentPeriod(createCurrentPeriodRestObject());

        StocksEntity stocksEntity = stocksTransformer
                .transform(stocks);

        assertNotNull(stocksEntity);
        assertNull(stocksEntity.getData().getPreviousPeriodEntity());
        validateStocksEntity(stocksEntity);
        assertEquals(new HashMap<>(), stocksEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithFullyPopulatedObject() {

        Stocks stocks = new Stocks();

        stocks.setEtag(ETAG);
        stocks.setKind(KIND);
        stocks.setLinks(new HashMap<>());
        stocks.setCurrentPeriod(createCurrentPeriodRestObject());
        stocks.setPreviousPeriod(createPreviousPeriodRestObject());

        StocksEntity stocksEntity = stocksTransformer.transform(stocks);

        assertNotNull(stocksEntity);
        validateStocksEntity(stocksEntity);
    }

    @Test
    @DisplayName("Tests transformer with empty entity object returns null values ")
    public void testTransformerWithEmptyEntityObject() {

        Stocks stocks = stocksTransformer.transform(new StocksEntity());

        assertNotNull(stocks);
        assertNull(stocks.getEtag());
        assertEquals(new HashMap<>(), stocks.getLinks());
    }

    @Test
    @DisplayName("Tests transformer with empty previous period Entity Object")
    public void testEntityToRestTransformerWithEmptyPreviousPeriodEntityObject() {

        StocksEntity stocksEntity = new StocksEntity();
        StocksDataEntity stocksDataEntity = new StocksDataEntity();

        stocksDataEntity.setEtag(ETAG);
        stocksDataEntity.setKind(KIND);
        stocksDataEntity.setLinks(new HashMap<>());
        stocksDataEntity.setCurrentPeriodEntity(createCurrentPeriodEntityObject());

        stocksEntity.setData(stocksDataEntity);

        Stocks stocks = stocksTransformer.transform(stocksEntity);

        assertNotNull(stocks);
        assertEqualsRestObject(stocks);
        assertEquals(new HashMap<>(), stocks.getLinks());
        assertEquals(ETAG, stocks.getEtag());
        assertEquals(KIND, stocks.getKind());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithFullyPopulatedEntityObject() {

        StocksEntity stocksEntity = new StocksEntity();
        StocksDataEntity stocksDataEntity = new StocksDataEntity();

        stocksDataEntity.setEtag(ETAG);
        stocksDataEntity.setKind(KIND);
        stocksDataEntity.setLinks(new HashMap<>());
        stocksDataEntity.setCurrentPeriodEntity(createCurrentPeriodEntityObject());
        stocksDataEntity.setPreviousPeriodEntity(createPreviousPeriodEntityObject());

        stocksEntity.setData(stocksDataEntity);

        Stocks stocks = stocksTransformer.transform(stocksEntity);

        assertNotNull(stocks);
        assertEqualsRestObject(stocks);
        assertEquals(new HashMap<>(), stocks.getLinks());
        assertEquals(ETAG, stocks.getEtag());
        assertEquals(KIND, stocks.getKind());
    }

    private PreviousPeriodEntity createPreviousPeriodEntityObject() {

        PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
        previousPeriodEntity.setPaymentsOnAccount(PAYMENTS_ON_ACCOUNT_PREVIOUS_PERIOD);
        previousPeriodEntity.setStocks(STOCKS_PREVIOUS_PERIOD);
        previousPeriodEntity.setTotal(TOTAL_PREVIOUS_PERIOD);

        return previousPeriodEntity;
    }

    private CurrentPeriodEntity createCurrentPeriodEntityObject() {

        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        currentPeriodEntity.setPaymentsOnAccount(PAYMENTS_ON_ACCOUNT_CURRENT_PERIOD);
        currentPeriodEntity.setStocks(STOCKS_CURRENT_PERIOD);
        currentPeriodEntity.setTotal(TOTAL_CURRENT_PERIOD);

        return currentPeriodEntity;
    }

    private PreviousPeriod createPreviousPeriodRestObject() {

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setPaymentsOnAccount(PAYMENTS_ON_ACCOUNT_PREVIOUS_PERIOD);
        previousPeriod.setStocks(STOCKS_PREVIOUS_PERIOD);
        previousPeriod.setTotal(TOTAL_PREVIOUS_PERIOD);

        return previousPeriod;
    }

    private CurrentPeriod createCurrentPeriodRestObject() {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setPaymentsOnAccount(PAYMENTS_ON_ACCOUNT_CURRENT_PERIOD);
        currentPeriod.setStocks(STOCKS_CURRENT_PERIOD);
        currentPeriod.setTotal(TOTAL_CURRENT_PERIOD);

        return currentPeriod;
    }

    private void validateStocksEntity(StocksEntity stocksEntity) {

        assertEquals(PAYMENTS_ON_ACCOUNT_CURRENT_PERIOD, stocksEntity.getData().getCurrentPeriodEntity().getPaymentsOnAccount());
        assertEquals(STOCKS_CURRENT_PERIOD, stocksEntity.getData().getCurrentPeriodEntity().getStocks());
        assertEquals(TOTAL_CURRENT_PERIOD, stocksEntity.getData().getCurrentPeriodEntity().getTotal());

        assertEquals(new HashMap<>(), stocksEntity.getData().getLinks());
        assertEquals(ETAG, stocksEntity.getData().getEtag());
        assertEquals(KIND, stocksEntity.getData().getKind());

        if (stocksEntity.getData().getPreviousPeriodEntity() != null) {
            assertEquals(PAYMENTS_ON_ACCOUNT_PREVIOUS_PERIOD, stocksEntity.getData().getPreviousPeriodEntity().getPaymentsOnAccount());
            assertEquals(STOCKS_PREVIOUS_PERIOD, stocksEntity.getData().getPreviousPeriodEntity().getStocks());
            assertEquals(TOTAL_PREVIOUS_PERIOD, stocksEntity.getData().getPreviousPeriodEntity().getTotal());

        }
    }

    private void assertEqualsRestObject(Stocks stocks) {

        assertEquals(PAYMENTS_ON_ACCOUNT_CURRENT_PERIOD, stocks.getCurrentPeriod().getPaymentsOnAccount());
        assertEquals(STOCKS_CURRENT_PERIOD, stocks.getCurrentPeriod().getStocks());
        assertEquals(TOTAL_CURRENT_PERIOD, stocks.getCurrentPeriod().getTotal());

        assertEquals(new HashMap<>(), stocks.getLinks());
        assertEquals(ETAG, stocks.getEtag());
        assertEquals(KIND, stocks.getKind());

        if (stocks.getPreviousPeriod() != null) {
            assertEquals(PAYMENTS_ON_ACCOUNT_PREVIOUS_PERIOD, stocks.getPreviousPeriod().getPaymentsOnAccount());
            assertEquals(STOCKS_PREVIOUS_PERIOD, stocks.getPreviousPeriod().getStocks());
            assertEquals(TOTAL_PREVIOUS_PERIOD, stocks.getPreviousPeriod().getTotal());
        }
    }
}