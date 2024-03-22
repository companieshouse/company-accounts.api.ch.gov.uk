package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.debtors.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.debtors.DebtorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.debtors.DebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.debtors.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.PreviousPeriod;

@Component
public class DebtorsTransformer implements NoteTransformer<Debtors, DebtorsEntity> {

    @Override
    public DebtorsEntity transform(Debtors rest) {
        DebtorsDataEntity debtorsDataEntity = new DebtorsDataEntity();
        DebtorsEntity debtorsEntity = new DebtorsEntity();

        if (rest.getCurrentPeriod() != null) {
            BeanUtils.copyProperties(rest, debtorsDataEntity);
            CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
            BeanUtils.copyProperties(rest.getCurrentPeriod(), currentPeriodEntity);
            debtorsDataEntity.setCurrentPeriodEntity(currentPeriodEntity);
        }

        if (rest.getPreviousPeriod() != null) {
            BeanUtils.copyProperties(rest, debtorsDataEntity);
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

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_DEBTORS;
    }
}
