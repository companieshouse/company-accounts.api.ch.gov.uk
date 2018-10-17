package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.*;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
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

            if(entity.getBalanceSheet().getCurrentAssets() != null){
                CurrentAssetsEntity currentAssetsEntity = new CurrentAssetsEntity();
                BeanUtils
                        .copyProperties(entity.getBalanceSheet().getCurrentAssets(), currentAssetsEntity);
                balanceSheetEntity.setCurrentAssets(currentAssetsEntity);

            }
        }

        previousPeriodDataEntity.setBalanceSheetEntity(balanceSheetEntity);
        previousPeriodEntity.setData(previousPeriodDataEntity);

        return previousPeriodEntity;
    }

    @Override
    public PreviousPeriod transform(PreviousPeriodEntity entity) {
        PreviousPeriod previousPeriod = new PreviousPeriod();
        PreviousPeriodDataEntity previousPeriodDataEntity = entity.getData();
        BalanceSheet balanceSheet = new BalanceSheet();

        BeanUtils.copyProperties(previousPeriodDataEntity, previousPeriod);
        if (previousPeriodDataEntity.getBalanceSheetEntity() != null) {
            BeanUtils.copyProperties(previousPeriodDataEntity.getBalanceSheetEntity(), balanceSheet);

            if (previousPeriodDataEntity.getBalanceSheetEntity().getFixedAssets() != null) {
                FixedAssets fixedAssets = new FixedAssets();
                BeanUtils.copyProperties(
                        previousPeriodDataEntity.getBalanceSheetEntity().getFixedAssets(), fixedAssets);
                balanceSheet.setFixedAssets(fixedAssets);
            }

            if (previousPeriodDataEntity.getBalanceSheetEntity().getCurrentAssets() != null) {
                CurrentAssets currentAssets = new CurrentAssets();
                BeanUtils.copyProperties(
                        previousPeriodDataEntity.getBalanceSheetEntity().getCurrentAssets(), currentAssets);
                balanceSheet.setCurrentAssets(currentAssets);
            }
        }

        previousPeriod.setBalanceSheet(balanceSheet);
        return previousPeriod;
    }
}