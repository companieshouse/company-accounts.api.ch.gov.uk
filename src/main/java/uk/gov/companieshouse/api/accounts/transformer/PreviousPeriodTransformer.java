package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.*;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;

@Component
public class PreviousPeriodTransformer implements
    GenericTransformer<PreviousPeriod, PreviousPeriodEntity> {

    @Override
    public PreviousPeriodEntity transform(PreviousPeriod entity) {
        PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
        PreviousPeriodDataEntity previousPeriodDataEntity = new PreviousPeriodDataEntity();
        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();

        BeanUtils.copyProperties(entity, previousPeriodDataEntity);
        if (entity.getBalanceSheet() != null) {
            BeanUtils.copyProperties(entity.getBalanceSheet(), balanceSheetEntity);
        }

        previousPeriodDataEntity.setBalanceSheetEntity(balanceSheetEntity);
        previousPeriodEntity.setData(previousPeriodDataEntity);

        return previousPeriodEntity;
    }

    @Override
    public PreviousPeriod transform(PreviousPeriodEntity entity) {
        return null;
    }
}