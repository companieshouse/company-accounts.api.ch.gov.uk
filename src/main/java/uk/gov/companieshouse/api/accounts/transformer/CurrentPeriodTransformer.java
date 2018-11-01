package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.*;
import uk.gov.companieshouse.api.accounts.model.rest.*;

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

            if (entity.getBalanceSheet().getFixedAssets() != null) {
                FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();
                BeanUtils
                    .copyProperties(entity.getBalanceSheet().getFixedAssets(), fixedAssetsEntity);
                balanceSheetEntity.setFixedAssets(fixedAssetsEntity);
            }

            if (entity.getBalanceSheet().getCurrentAssets() != null) {
                CurrentAssetsEntity currentAssetsEntity = new CurrentAssetsEntity();
                BeanUtils
                        .copyProperties(entity.getBalanceSheet().getCurrentAssets(), currentAssetsEntity);
                balanceSheetEntity.setCurrentAssets(currentAssetsEntity);
            }

            if (entity.getBalanceSheet().getCapitalAndReserves() != null) {
                CapitalAndReservesEntity capitalAndReservesEntity = new CapitalAndReservesEntity();
                BeanUtils
                        .copyProperties(entity.getBalanceSheet().getCapitalAndReserves(), capitalAndReservesEntity);
                balanceSheetEntity.setCapitalAndReservesEntity(capitalAndReservesEntity);
            }
        }

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
            BeanUtils.copyProperties(currentPeriodDataEntity.getBalanceSheetEntity(), balanceSheet);

            if (currentPeriodDataEntity.getBalanceSheetEntity().getFixedAssets() != null) {
                FixedAssets fixedAssets = new FixedAssets();
                BeanUtils.copyProperties(
                    currentPeriodDataEntity.getBalanceSheetEntity().getFixedAssets(), fixedAssets);
                balanceSheet.setFixedAssets(fixedAssets);
            }

            if (currentPeriodDataEntity.getBalanceSheetEntity().getCurrentAssets() != null) {
                CurrentAssets currentAssets  = new CurrentAssets();
                BeanUtils.copyProperties(
                        currentPeriodDataEntity.getBalanceSheetEntity().getCurrentAssets(), currentAssets);
                balanceSheet.setCurrentAssets(currentAssets);
            }

            if (currentPeriodDataEntity.getBalanceSheetEntity().getCapitalAndReservesEntity() != null) {
                CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
                BeanUtils.copyProperties(
                        currentPeriodDataEntity.getBalanceSheetEntity().getCapitalAndReservesEntity(), capitalAndReserves);
                balanceSheet.setCapitalAndReserves(capitalAndReserves);
            }
        }

        currentPeriod.setBalanceSheet(balanceSheet);
        return currentPeriod;
    }
}
