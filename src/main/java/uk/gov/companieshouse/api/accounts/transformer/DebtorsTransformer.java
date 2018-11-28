package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodDebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.DebtorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.DebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodDebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriodDebtors;
import uk.gov.companieshouse.api.accounts.model.rest.Debtors;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriodDebtors;

public class DebtorsTransformer  implements GenericTransformer<Debtors, DebtorsEntity> {

    @Override
    public DebtorsEntity transform(Debtors rest) {

        DebtorsDataEntity debtorsDataEntity = new DebtorsDataEntity();
        DebtorsEntity debtorsEntity = new DebtorsEntity();
        CurrentPeriodDebtorsEntity currentPeriodDebtorsEntity = new CurrentPeriodDebtorsEntity();
        PreviousPeriodDebtorsEntity previousPeriodDebtorsEntity = new PreviousPeriodDebtorsEntity();

        BeanUtils.copyProperties(rest, debtorsDataEntity);
        if (rest.getCurrentPeriodDebtors() != null) {
            BeanUtils.copyProperties(rest.getCurrentPeriodDebtors(), currentPeriodDebtorsEntity);
        }

        BeanUtils.copyProperties(rest, debtorsDataEntity);
        if (rest.getPreviousPeriodDebtors() != null) {
            BeanUtils.copyProperties(rest.getPreviousPeriodDebtors(), previousPeriodDebtorsEntity);
        }

        debtorsDataEntity.setCurrentPeriodDebtorsEntity(currentPeriodDebtorsEntity);
        debtorsDataEntity.setPreviousPeriodDebtorsEntity(previousPeriodDebtorsEntity);

        debtorsEntity.setData(debtorsDataEntity);

        return debtorsEntity;
    }

    @Override
    public Debtors transform(DebtorsEntity entity) {

        Debtors debtors = new Debtors();

        DebtorsDataEntity debtorsDataEntity = entity.getData();

        BeanUtils.copyProperties(debtorsDataEntity, debtors);

        if (debtorsDataEntity.getCurrentPeriodDebtorsEntity() != null) {
            CurrentPeriodDebtors currentPeriodDebtors = new CurrentPeriodDebtors();
            BeanUtils.copyProperties(debtorsDataEntity.getCurrentPeriodDebtorsEntity(), currentPeriodDebtors);
            debtors.setCurrentPeriodDebtors(currentPeriodDebtors);
        }

        if (debtorsDataEntity.getPreviousPeriodDebtorsEntity() != null) {
            PreviousPeriodDebtors previousPeriodDebtors = new PreviousPeriodDebtors();
            BeanUtils.copyProperties(debtorsDataEntity.getPreviousPeriodDebtorsEntity(), previousPeriodDebtors);
            debtors.setPreviousPeriodDebtors(previousPeriodDebtors);
        }
            return debtors;
        }
}
