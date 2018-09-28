package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.BalanceSheetEntity;
import uk.gov.companieshouse.api.accounts.model.entity.FixedAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
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

            if (entity.getBalanceSheet().getFixedAssets() != null) {
                FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();
                BeanUtils
                    .copyProperties(entity.getBalanceSheet().getFixedAssets(), fixedAssetsEntity);
                balanceSheetEntity.setFixedAssets(fixedAssetsEntity);
            }
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