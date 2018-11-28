package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.BalanceSheetEntity;
import uk.gov.companieshouse.api.accounts.model.entity.FixedAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.OtherLiabilitiesOrAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CapitalAndReservesEntity;

import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.CapitalAndReserves;


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

            // OtherLiabilitiesOrAssetsEntity
            if (entity.getBalanceSheet().getOtherLiabilitiesOrAssets() != null) {
                OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssetsEntity = new OtherLiabilitiesOrAssetsEntity();
                BeanUtils
                        .copyProperties(entity.getBalanceSheet().getOtherLiabilitiesOrAssets(), otherLiabilitiesOrAssetsEntity);
                balanceSheetEntity.setOtherLiabilitiesOrAssetsEntity(otherLiabilitiesOrAssetsEntity);
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


            // OtherLiabilitiesOrAssetsEntity
            if (previousPeriodDataEntity.getBalanceSheetEntity().getOtherLiabilitiesOrAssetsEntity() != null) {
                OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
                BeanUtils.copyProperties(
                        previousPeriodDataEntity.getBalanceSheetEntity().getOtherLiabilitiesOrAssetsEntity(), otherLiabilitiesOrAssets);
                balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
            }

            if (previousPeriodDataEntity.getBalanceSheetEntity().getCurrentAssets() != null) {
                CurrentAssets currentAssets = new CurrentAssets();
                BeanUtils.copyProperties(
                        previousPeriodDataEntity.getBalanceSheetEntity().getCurrentAssets(), currentAssets);
                balanceSheet.setCurrentAssets(currentAssets);

            }

            if (previousPeriodDataEntity.getBalanceSheetEntity().getCapitalAndReservesEntity() != null) {
                CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
                BeanUtils.copyProperties(
                        previousPeriodDataEntity.getBalanceSheetEntity().getCapitalAndReservesEntity(), capitalAndReserves);
                balanceSheet.setCapitalAndReserves(capitalAndReserves);
            }
        }

        previousPeriod.setBalanceSheet(balanceSheet);
        return previousPeriod;
    }
}