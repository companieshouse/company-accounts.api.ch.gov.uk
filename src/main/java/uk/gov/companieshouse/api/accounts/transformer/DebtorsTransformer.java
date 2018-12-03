package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.DebtorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.DebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.PreviousPeriod;

@Component
public class DebtorsTransformer implements GenericTransformer<Debtors, DebtorsEntity> {

    @Override
    public DebtorsEntity transform(Debtors rest) {

        DebtorsDataEntity debtorsDataEntity = new DebtorsDataEntity();
        DebtorsEntity debtorsEntity = new DebtorsEntity();

        BeanUtils.copyProperties(rest, debtorsDataEntity);
        if (rest.getCurrentPeriod() != null) {
            CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
            BeanUtils.copyProperties(rest.getCurrentPeriod(), currentPeriodEntity);
            debtorsDataEntity.setCurrentPeriodEntity(currentPeriodEntity);
        }

        BeanUtils.copyProperties(rest, debtorsDataEntity);
        if (rest.getPreviousPeriod() != null) {
            PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
            BeanUtils.copyProperties(rest.getPreviousPeriod(), previousPeriodEntity);
            debtorsDataEntity.setPreviousPeriodEntity(previousPeriodEntity);
        }

        debtorsEntity.setData(debtorsDataEntity);

        return debtorsEntity;
    }

    @Override
    public Debtors transform(DebtorsEntity entity) {

        Debtors debtors = new Debtors();
        DebtorsDataEntity debtorsDataEntity = entity.getData();

        BeanUtils.copyProperties(debtorsDataEntity, debtors);

        if (debtorsDataEntity.getCurrentPeriodEntity() != null) {
            CurrentPeriod currentPeriod = new CurrentPeriod();
            BeanUtils.copyProperties(debtorsDataEntity.getCurrentPeriodEntity(), currentPeriod);
            debtors.setCurrentPeriod(currentPeriod);
        }

        if (debtorsDataEntity.getPreviousPeriodEntity() != null) {
            PreviousPeriod previousPeriod = new PreviousPeriod();
            BeanUtils.copyProperties(debtorsDataEntity.getPreviousPeriodEntity(), previousPeriod);
            debtors.setPreviousPeriod(previousPeriod);
        }
        return debtors;
    }
}
