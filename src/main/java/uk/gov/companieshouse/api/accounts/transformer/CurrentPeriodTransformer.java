package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.BalanceSheetEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.FixedAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;

@Component
public class CurrentPeriodTransformer implements
        GenericTransformer<CurrentPeriod, CurrentPeriodEntity> {

    @Override
    public CurrentPeriodEntity transform(CurrentPeriod entity) {
        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        CurrentPeriodDataEntity currentPeriodDataEntity = new CurrentPeriodDataEntity();
        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();

        BeanUtils.copyProperties(entity, currentPeriodDataEntity);
        if (entity.getBalanceSheet() != null) {
            BeanUtils.copyProperties(entity.getBalanceSheet(), balanceSheetEntity);
        }

        if(entity.getBalanceSheet().getFixedAssets() !=null ) {
            BeanUtils.copyProperties(entity.getBalanceSheet().getFixedAssets(), fixedAssetsEntity);
        }

        balanceSheetEntity.setFixedAssets(fixedAssetsEntity);
        currentPeriodDataEntity.setBalanceSheetEntity(balanceSheetEntity);
        currentPeriodEntity.setData(currentPeriodDataEntity);

        return currentPeriodEntity;
    }

    @Override
    public CurrentPeriod transform(CurrentPeriodEntity entity) {
        CurrentPeriod currentPeriod = new CurrentPeriod();
        CurrentPeriodDataEntity currentPeriodDataEntity = entity.getData();
        BalanceSheet balanceSheet = new BalanceSheet();

        BeanUtils.copyProperties(currentPeriodDataEntity, currentPeriod);
        if (currentPeriodDataEntity.getBalanceSheetEntity() != null) {
            BeanUtils.copyProperties(currentPeriodDataEntity.getBalanceSheetEntity(),  balanceSheet);
        }

        currentPeriod.setBalanceSheet(balanceSheet);
        return currentPeriod;
    }
}
