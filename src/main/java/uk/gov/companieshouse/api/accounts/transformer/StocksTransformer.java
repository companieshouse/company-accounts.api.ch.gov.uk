package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.notes.stocks.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.stocks.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.stocks.StocksDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.stocks.StocksEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;

@Component
public class StocksTransformer implements GenericTransformer<Stocks, StocksEntity> {

    @Override
    public StocksEntity transform(Stocks rest) {

        StocksDataEntity stocksDataEntity = new StocksDataEntity();
        StocksEntity stocksEntity = new StocksEntity();

        BeanUtils.copyProperties(rest, stocksDataEntity);
        if (rest.getCurrentPeriod() != null) {
            CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
            BeanUtils.copyProperties(rest.getCurrentPeriod(), currentPeriodEntity);
            stocksDataEntity.setCurrentPeriodEntity(currentPeriodEntity);
        }

        BeanUtils.copyProperties(rest, stocksDataEntity);
        if (rest.getPreviousPeriod() != null) {
            PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
            BeanUtils.copyProperties(rest.getPreviousPeriod(), previousPeriodEntity);
            stocksDataEntity.setPreviousPeriodEntity(previousPeriodEntity);
        }

        stocksEntity.setData(stocksDataEntity);

        return stocksEntity;
    }

    @Override
    public Stocks transform(StocksEntity entity) {

        Stocks stocks = new Stocks();
        StocksDataEntity stocksDataEntity;

        if (entity.getData() != null) {
            stocksDataEntity = entity.getData();
        } else {
            stocksDataEntity = new StocksDataEntity();
        }

        BeanUtils.copyProperties(stocksDataEntity, stocks);

        if (stocksDataEntity.getCurrentPeriodEntity() != null) {
            CurrentPeriod currentPeriod = new CurrentPeriod();
            BeanUtils.copyProperties(stocksDataEntity.getCurrentPeriodEntity(), currentPeriod);
            stocks.setCurrentPeriod(currentPeriod);
        }

        if (stocksDataEntity.getPreviousPeriodEntity() != null) {
            PreviousPeriod previousPeriod = new PreviousPeriod();
            BeanUtils.copyProperties(stocksDataEntity.getPreviousPeriodEntity(), previousPeriod);
            stocks.setPreviousPeriod(previousPeriod);
        }

        return stocks;
    }
}
