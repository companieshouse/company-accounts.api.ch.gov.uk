package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.BalanceSheetEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.FixedAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.MembersFundsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.OtherLiabilitiesOrAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CapitalAndReservesEntity;

import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.CapitalAndReserves;
import uk.gov.companieshouse.api.accounts.model.rest.MembersFunds;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;

@Component
public class CurrentPeriodTransformer implements GenericTransformer<CurrentPeriod, CurrentPeriodEntity> {

    @Override
    public CurrentPeriodEntity transform(CurrentPeriod entity) {
        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        CurrentPeriodDataEntity currentPeriodDataEntity = new CurrentPeriodDataEntity();
        BeanUtils.copyProperties(entity, currentPeriodDataEntity);

        if (entity.getBalanceSheet() != null) {
            BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
            BeanUtils.copyProperties(entity.getBalanceSheet(), balanceSheetEntity);

            if (entity.getBalanceSheet().getFixedAssets() != null) {
                FixedAssetsEntity fixedAssetsEntity = new FixedAssetsEntity();
                BeanUtils.copyProperties(entity.getBalanceSheet().getFixedAssets(), fixedAssetsEntity);
                balanceSheetEntity.setFixedAssets(fixedAssetsEntity);
            }

            if (entity.getBalanceSheet().getOtherLiabilitiesOrAssets() != null) {
                OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssetsEntity = new OtherLiabilitiesOrAssetsEntity();
                BeanUtils.copyProperties(
                        entity.getBalanceSheet().getOtherLiabilitiesOrAssets(), otherLiabilitiesOrAssetsEntity);
                balanceSheetEntity.setOtherLiabilitiesOrAssetsEntity(otherLiabilitiesOrAssetsEntity);
            }

            if (entity.getBalanceSheet().getCurrentAssets() != null) {
                CurrentAssetsEntity currentAssetsEntity = new CurrentAssetsEntity();
                BeanUtils.copyProperties(entity.getBalanceSheet().getCurrentAssets(), currentAssetsEntity);
                balanceSheetEntity.setCurrentAssets(currentAssetsEntity);
            }

            if (entity.getBalanceSheet().getCapitalAndReserves() != null) {
                CapitalAndReservesEntity capitalAndReservesEntity = new CapitalAndReservesEntity();
                BeanUtils.copyProperties(entity.getBalanceSheet().getCapitalAndReserves(), capitalAndReservesEntity);
                balanceSheetEntity.setCapitalAndReservesEntity(capitalAndReservesEntity);
            }

            if (entity.getBalanceSheet().getMembersFunds() != null) {
                MembersFundsEntity membersFundsEntity = new MembersFundsEntity();
                BeanUtils.copyProperties(entity.getBalanceSheet().getMembersFunds(), membersFundsEntity);
                balanceSheetEntity.setMembersFundsEntity(membersFundsEntity);
            }

            currentPeriodDataEntity.setBalanceSheetEntity(balanceSheetEntity);
        }

        currentPeriodEntity.setData(currentPeriodDataEntity);

        return currentPeriodEntity;
    }

    @Override
    public CurrentPeriod transform(CurrentPeriodEntity entity) {
        CurrentPeriod currentPeriod = new CurrentPeriod();
        CurrentPeriodDataEntity currentPeriodDataEntity = entity.getData();
        BeanUtils.copyProperties(currentPeriodDataEntity, currentPeriod);

        if (currentPeriodDataEntity.getBalanceSheetEntity() != null) {
            BalanceSheet balanceSheet = new BalanceSheet();
            BeanUtils.copyProperties(currentPeriodDataEntity.getBalanceSheetEntity(), balanceSheet);

            if (currentPeriodDataEntity.getBalanceSheetEntity().getFixedAssets() != null) {
                FixedAssets fixedAssets = new FixedAssets();
                BeanUtils.copyProperties(currentPeriodDataEntity.getBalanceSheetEntity().getFixedAssets(), fixedAssets);
                balanceSheet.setFixedAssets(fixedAssets);
            }

            if (currentPeriodDataEntity.getBalanceSheetEntity().getOtherLiabilitiesOrAssetsEntity() != null) {
                OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
                BeanUtils.copyProperties(currentPeriodDataEntity.getBalanceSheetEntity()
                        .getOtherLiabilitiesOrAssetsEntity(), otherLiabilitiesOrAssets);
                balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
            }

            if (currentPeriodDataEntity.getBalanceSheetEntity().getCurrentAssets() != null) {
                CurrentAssets currentAssets = new CurrentAssets();
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

            if (currentPeriodDataEntity.getBalanceSheetEntity().getMembersFundsEntity() != null) {
                MembersFunds membersFunds = new MembersFunds();
                BeanUtils.copyProperties(
                        currentPeriodDataEntity.getBalanceSheetEntity().getMembersFundsEntity(), membersFunds);
                balanceSheet.setMembersFunds(membersFunds);
            }

            currentPeriod.setBalanceSheet(balanceSheet);
        }

        return currentPeriod;

    }
}
