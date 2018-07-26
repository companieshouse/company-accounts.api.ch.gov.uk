package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.BalanceSheetEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;

@Component
public class CurrentPeriodTransformer implements
        GenericTransformer<CurrentPeriod, CurrentPeriodEntity> {

    @Override
    public CurrentPeriodEntity transform(CurrentPeriod entity) {
        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        CurrentPeriodDataEntity currentPeriodDataEntity = new CurrentPeriodDataEntity();
        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();

        BeanUtils.copyProperties(entity, currentPeriodDataEntity);
        if (entity.getBalanceSheet() != null) {
            BeanUtils.copyProperties(entity.getBalanceSheet(), balanceSheetEntity);
        }

        currentPeriodDataEntity.setBalanceSheetEntity(balanceSheetEntity);
        currentPeriodEntity.setData(currentPeriodDataEntity);

        return currentPeriodEntity;
    }

    @Override
    public CurrentPeriod transform(CurrentPeriodEntity entity) {
        return null;
    }
}
