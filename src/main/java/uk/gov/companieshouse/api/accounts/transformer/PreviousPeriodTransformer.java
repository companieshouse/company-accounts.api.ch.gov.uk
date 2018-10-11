package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.BalanceSheetEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
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
        }

        previousPeriod.setBalanceSheet(balanceSheet);
        return previousPeriod;
    }
}